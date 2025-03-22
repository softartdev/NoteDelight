package com.softartdev.notedelight

import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.repository.SafeRepo

/**
 * Encryption functions are mocked
 */
class JvmTestSafeRepo : SafeRepo() {
    @Volatile
    private var databaseHolder: JdbcDatabaseTestHolder? = buildDbIfNeed()

    override val databaseState: PlatformSQLiteState
        get() = TODO("Not yet implemented")

    override val noteDAO: NoteDAO
        get() = TODO()

    override val dbPath: String
        get() = TODO("Not yet implemented")

    override fun buildDbIfNeed(passphrase: CharSequence): JdbcDatabaseTestHolder = synchronized(this) {
        var instance = databaseHolder
        if (instance == null) {
            instance = JdbcDatabaseTestHolder()
            databaseHolder = instance
        }
        return instance
    }

    override fun decrypt(oldPass: CharSequence) {
        closeDatabase()
        buildDbIfNeed()
    }

    override fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        closeDatabase()
        buildDbIfNeed(newPass)
    }

    override fun encrypt(newPass: CharSequence) {
        closeDatabase()
        buildDbIfNeed(newPass)
    }

    override fun execute(query: String): String? {
        return TODO()
    }

    override fun closeDatabase() = synchronized(this) {
        databaseHolder?.close()
        databaseHolder = null
    }
}