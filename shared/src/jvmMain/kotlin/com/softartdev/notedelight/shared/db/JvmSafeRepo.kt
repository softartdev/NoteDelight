package com.softartdev.notedelight.shared.db

import com.softartdev.notedelight.shared.JvmCipherUtils
import com.softartdev.notedelight.shared.PlatformSQLiteState
import java.util.Properties

class JvmSafeRepo : SafeRepo() {
    @Volatile
    private var databaseHolder: DatabaseHolder? = null

    override val databaseState: PlatformSQLiteState
        get() = JvmCipherUtils.getDatabaseState(DB_NAME)

    override val noteDAO: NoteDAO
        get() = NoteDAO(buildDbIfNeed().noteQueries)

    override fun buildDbIfNeed(passphrase: CharSequence): DatabaseHolder {
        var instance = databaseHolder
        if (instance == null) {
            val properties = Properties()
            if (passphrase.isNotEmpty()) properties["password"] = StringBuilder(passphrase).toString()
            instance = JdbcDatabaseHolder(properties)
            databaseHolder = instance
        }
        return instance
    }

    override fun decrypt(oldPass: CharSequence) {
        closeDatabase()
        JvmCipherUtils.decrypt(
            password = StringBuilder(oldPass).toString(),
            dbName = DB_NAME
        )
        buildDbIfNeed()
    }

    override fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        decrypt(oldPass)
        encrypt(newPass)
    }

    override fun encrypt(newPass: CharSequence) {
        closeDatabase()
        JvmCipherUtils.encrypt(
            password = StringBuilder(newPass).toString(),
            dbName = DB_NAME
        )
        buildDbIfNeed(newPass)
    }

    override fun closeDatabase() = synchronized(this) {
        databaseHolder?.close()
        databaseHolder = null
    }
}