package com.softartdev.notedelight

import androidx.room3.useReaderConnection
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.db.NoteDatabase
import com.softartdev.notedelight.db.RoomNoteDAO
import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.repository.SafeRepo

/**
 * Encryption functions are mocked
 */
class JvmTestSafeRepo : SafeRepo() {
    @Volatile
    private var databaseHolder: JdbcDatabaseTestHolder? = null

    private val noteDatabase: NoteDatabase
        get() = requireNotNull(databaseHolder).noteDatabase

    override var databaseState: PlatformSQLiteState = PlatformSQLiteState.UNENCRYPTED
        private set

    override val noteDAO: NoteDAO
        get() = RoomNoteDAO(this@JvmTestSafeRepo::noteDatabase)

    override val dbPath: String = ":memory:"

    override suspend fun buildDbIfNeed(passphrase: CharSequence): JdbcDatabaseTestHolder = synchronized(this) {
        var instance = databaseHolder
        if (instance == null) {
            instance = JdbcDatabaseTestHolder()
            databaseHolder = instance
        }
        databaseState = when {
            passphrase.isEmpty() -> PlatformSQLiteState.UNENCRYPTED
            else -> PlatformSQLiteState.ENCRYPTED
        }
        return instance
    }

    override suspend fun decrypt(oldPass: CharSequence) {
        closeDatabase()
        buildDbIfNeed()
    }

    override suspend fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        closeDatabase()
        buildDbIfNeed(newPass)
    }

    override suspend fun encrypt(newPass: CharSequence) {
        closeDatabase()
        buildDbIfNeed(newPass)
    }

    override suspend fun execute(query: String): String? {
        buildDbIfNeed()
        return noteDatabase.useReaderConnection { connection ->
            connection.usePrepared(query) { statement ->
                if (statement.step()) statement.getText(0) else null
            }
        }
    }

    override suspend fun closeDatabase() = synchronized(this) {
        databaseHolder?.close()
        databaseHolder = null
    }
}
