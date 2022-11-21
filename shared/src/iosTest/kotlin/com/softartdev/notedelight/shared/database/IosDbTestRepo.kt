package com.softartdev.notedelight.shared.database

import com.softartdev.notedelight.shared.PlatformSQLiteState
import com.softartdev.notedelight.shared.data.PlatformSQLiteThrowable
import com.softartdev.notedelight.shared.db.NoteQueries

class IosDbTestRepo : DatabaseRepo() {

    private var dbHolder: DatabaseHolder? = buildDatabaseInstanceIfNeed()

    override val databaseState: PlatformSQLiteState
        get() = TODO("Not yet implemented")

    override val noteQueries: NoteQueries
        get() = dbHolder?.noteQueries ?: throw PlatformSQLiteThrowable("DB is null")

    override fun buildDatabaseInstanceIfNeed(passphrase: CharSequence): DatabaseHolder {
        if (dbHolder != null) {
            return dbHolder!!
        }
        val passkey = if (passphrase.isEmpty()) null else passphrase.toString()
        dbHolder = IosDatabaseTestHolder(
            key = passkey,
            rekey = passkey
        )
        return dbHolder!!
    }

    override fun decrypt(oldPass: CharSequence) {
        closeDatabase()
        dbHolder = IosDatabaseTestHolder(key = oldPass.toString())
    }

    override fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        closeDatabase()
        dbHolder = IosDatabaseTestHolder(key = oldPass.toString(), rekey = newPass.toString())
    }

    override fun encrypt(newPass: CharSequence) {
        closeDatabase()
        dbHolder = IosDatabaseTestHolder(rekey = newPass.toString())
    }

    override fun closeDatabase() {
        dbHolder?.close()
        dbHolder = null
    }
}