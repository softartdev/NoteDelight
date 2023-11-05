package com.softartdev.notedelight.shared.db

import com.softartdev.notedelight.shared.IosCipherUtils
import com.softartdev.notedelight.shared.PlatformSQLiteState

class IosSafeRepo : SafeRepo() {

    private var dbHolder: DatabaseHolder? = null

    override val databaseState: PlatformSQLiteState
        get() = IosCipherUtils.getDatabaseState(DB_NAME)

    override val noteDAO: NoteDAO
        get() = NoteDAO(buildDbIfNeed().noteQueries)

    override fun buildDbIfNeed(passphrase: CharSequence): DatabaseHolder {
        var instance = dbHolder
        if (instance == null) {
            val passCopy: String? = if (passphrase.isNotEmpty()) passphrase.toString() else null
            instance = IosDatabaseHolder(key = passCopy)
            dbHolder = instance
        }
        return instance
    }

    override fun decrypt(oldPass: CharSequence) {
        closeDatabase()
        IosCipherUtils.decrypt(oldPass.toString(), DB_NAME)
        dbHolder = IosDatabaseHolder()
    }

    override fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        closeDatabase()
        dbHolder = IosDatabaseHolder(key = oldPass.toString(), rekey = newPass.toString())
        dbHolder?.driver?.execute(null, "VACUUM;", 0)
    }

    override fun encrypt(newPass: CharSequence) {
        closeDatabase()
        IosCipherUtils.encrypt(newPass.toString(), DB_NAME)
        dbHolder = IosDatabaseHolder(key = newPass.toString())
    }

    override fun closeDatabase() {
        dbHolder?.close()
        dbHolder = null
    }
}