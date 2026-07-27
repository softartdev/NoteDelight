package com.softartdev.notedelight.repository

import androidx.room3.useWriterConnection
import com.softartdev.notedelight.db.FilePathResolver
import com.softartdev.notedelight.db.JdbcDatabaseHolder
import com.softartdev.notedelight.db.JvmCipherUtils
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.db.NoteDatabase
import com.softartdev.notedelight.db.RoomNoteDAO
import com.softartdev.notedelight.model.PlatformSQLiteState
import java.util.Properties

class JvmSafeRepo : SafeRepo() {
    @Volatile
    private var databaseHolder: JdbcDatabaseHolder? = null
    private var dbPathOverride: String? = null

    private val noteDatabase: NoteDatabase
        get() = requireNotNull(databaseHolder).noteDatabase

    override val databaseState: PlatformSQLiteState
        get() = JvmCipherUtils.getDatabaseState(dbPath)

    override val noteDAO: NoteDAO
        get() = RoomNoteDAO(this@JvmSafeRepo::noteDatabase)

    override val dbPath: String
        get() = dbPathOverride ?: FilePathResolver().invoke()

    internal fun overrideDbPath(dbPath: String) {
        dbPathOverride = dbPath
    }

    override suspend fun buildDbIfNeed(passphrase: CharSequence): JdbcDatabaseHolder {
        var instance = databaseHolder
        if (instance == null) {
            val properties = Properties()
            if (passphrase.isNotEmpty()) properties["password"] = StringBuilder(passphrase).toString()
            instance = JdbcDatabaseHolder(properties, dbPath)
            databaseHolder = instance
        }
        return instance
    }

    override suspend fun decrypt(oldPass: CharSequence) {
        closeDatabase()
        JvmCipherUtils.decrypt(
            password = StringBuilder(oldPass).toString(),
            dbName = dbPath
        )
        buildDbIfNeed()
    }

    override suspend fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        decrypt(oldPass)
        encrypt(newPass)
    }

    override suspend fun execute(query: String): String? {
        buildDbIfNeed()
        return noteDatabase.useWriterConnection { connection ->
            connection.usePrepared(query) { statement ->
                if (statement.step()) statement.getText(0) else null
            }
        }
    }

    override suspend fun encrypt(newPass: CharSequence) {
        closeDatabase()
        JvmCipherUtils.encrypt(
            password = StringBuilder(newPass).toString(),
            dbName = dbPath
        )
        buildDbIfNeed(newPass)
    }

    override suspend fun closeDatabase() = synchronized(this) {
        databaseHolder?.close()
        databaseHolder = null
    }
}
