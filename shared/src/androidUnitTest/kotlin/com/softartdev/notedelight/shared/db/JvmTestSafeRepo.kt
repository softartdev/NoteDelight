package com.softartdev.notedelight.shared.db

import com.softartdev.notedelight.shared.PlatformSQLiteState

/**
 * Encryption functions are mocked
 */
class JvmTestSafeRepo : SafeRepo() {
    @Volatile
    private var databaseHolder: DatabaseHolder? = buildDbIfNeed()

    override val databaseState: PlatformSQLiteState
        get() = TODO("Not yet implemented")

    override val noteDAO: NoteDAO
        get() = NoteDAO(databaseHolder?.noteQueries ?: throw PlatformSQLiteThrowable("DB is null"))

    override val dbPath: String
        get() = TODO("Not yet implemented")

    override fun buildDbIfNeed(passphrase: CharSequence): DatabaseHolder = synchronized(this) {
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

    override fun closeDatabase() = synchronized(this) {
        databaseHolder?.close()
        databaseHolder = null
    }
}