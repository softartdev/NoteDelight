package com.softartdev.notedelight.repository

import androidx.sqlite.SQLiteException
import androidx.room3.useReaderConnection
import androidx.room3.useWriterConnection
import androidx.sqlite.SQLiteStatement
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.db.NoteDatabase
import com.softartdev.notedelight.db.RoomNoteDAO
import com.softartdev.notedelight.db.WebDatabaseHolder
import com.softartdev.notedelight.model.PlatformSQLiteState

class WebSafeRepo : SafeRepo() {
    private var dbHolder: WebDatabaseHolder? = null

    private val noteDatabase: NoteDatabase
        get() = requireNotNull(dbHolder).noteDatabase

    override var databaseState: PlatformSQLiteState = PlatformSQLiteState.DOES_NOT_EXIST
        private set

    override val noteDAO: NoteDAO
        get() = RoomNoteDAO(this@WebSafeRepo::noteDatabase)

    override val dbPath: String = DB_NAME

    override suspend fun buildDbIfNeed(passphrase: CharSequence): WebDatabaseHolder {
        dbHolder?.let { return it }

        val holder = WebDatabaseHolder(passphrase.takeIf(CharSequence::isNotEmpty)?.toString())
        try {
            holder.noteDatabase.useReaderConnection { connection ->
                connection.usePrepared("SELECT count(*) FROM sqlite_master") { statement ->
                    check(statement.step()) { "Database readability probe returned no row" }
                    statement.getLong(0)
                }
            }
        } catch (throwable: Throwable) {
            holder.shutdown()
            if (passphrase.isEmpty() && throwable.isEncryptedDatabaseError()) {
                databaseState = PlatformSQLiteState.ENCRYPTED
            }
            throw throwable
        }

        dbHolder = holder
        databaseState = when {
            passphrase.isEmpty() -> PlatformSQLiteState.UNENCRYPTED
            else -> PlatformSQLiteState.ENCRYPTED
        }
        return holder
    }

    override suspend fun decrypt(oldPass: CharSequence) {
        val holder = dbHolder ?: buildDbIfNeed(oldPass)
        holder.executePragma("PRAGMA rekey = ''")
        closeDatabase()
        buildDbIfNeed()
    }

    override suspend fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        val holder = dbHolder ?: buildDbIfNeed(oldPass)
        holder.executePragma("PRAGMA rekey = '${newPass.sqlLiteral()}'")
        closeDatabase()
        buildDbIfNeed(newPass)
    }

    override suspend fun encrypt(newPass: CharSequence) {
        val holder = dbHolder ?: buildDbIfNeed()
        holder.normalizePageSizeForSqlCipher()
        holder.executePragma("PRAGMA cipher = 'sqlcipher'")
        holder.executePragma("PRAGMA legacy = 4")
        holder.executePragma("PRAGMA rekey = '${newPass.sqlLiteral()}'")
        closeDatabase()
        buildDbIfNeed(newPass)
    }

    override suspend fun execute(query: String): String? {
        buildDbIfNeed()
        return noteDatabase.useWriterConnection { connection ->
            connection.usePrepared(query) { statement ->
                if (statement.step()) statement.getText(0) else null
            }
        }
    }

    override suspend fun closeDatabase() {
        val holder = dbHolder ?: return
        dbHolder = null
        holder.shutdown()
    }
}

private suspend fun WebDatabaseHolder.executePragma(sql: String) {
    noteDatabase.useWriterConnection { connection ->
        connection.usePrepared(sql, SQLiteStatement::step)
    }
}

private suspend fun WebDatabaseHolder.normalizePageSizeForSqlCipher() {
    executePragma("PRAGMA journal_mode = DELETE")
    executePragma("PRAGMA page_size = 4096")
    executePragma("VACUUM")
}

private fun CharSequence.sqlLiteral(): String = toString().replace("'", "''")

private fun Throwable.isEncryptedDatabaseError(): Boolean {
    var throwable: Throwable? = this
    while (throwable != null) {
        if (
            throwable is SQLiteException &&
            throwable.message?.startsWith("Error code: 26") == true
        ) {
            return true
        }
        throwable = throwable.cause
    }
    return false
}
