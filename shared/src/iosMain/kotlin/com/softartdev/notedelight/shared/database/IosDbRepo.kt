package com.softartdev.notedelight.shared.database

import com.softartdev.notedelight.shared.PlatformSQLiteState
import com.softartdev.notedelight.shared.IosCipherUtils
import com.softartdev.notedelight.shared.data.PlatformSQLiteThrowable
import com.softartdev.notedelight.shared.db.NoteQueries

class IosDbRepo : DatabaseRepo() {

    private var dbHolder: DatabaseHolder? = buildDatabaseInstanceIfNeed()

    override val databaseState: PlatformSQLiteState
        get() = IosCipherUtils.getDatabaseState(DB_NAME)

    override val noteQueries: NoteQueries
        get() = dbHolder?.noteQueries ?: throw PlatformSQLiteThrowable("DB is null")

    override fun buildDatabaseInstanceIfNeed(passphrase: CharSequence): DatabaseHolder {
        if (dbHolder != null) {
            return dbHolder!!
        }
        val passkey = if (passphrase.isEmpty()) null else passphrase.toString()
        dbHolder = IosDatabaseHolder(
            key = passkey,
            rekey = passkey
        )
        return dbHolder!!
    }

    override fun decrypt(oldPass: CharSequence) {
        closeDatabase()
        IosCipherUtils.decrypt(oldPass.toString(), DB_NAME)
        dbHolder = IosDatabaseHolder()
    }

    override fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        closeDatabase()
        dbHolder = IosDatabaseHolder(key = oldPass.toString(), rekey = newPass.toString())
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