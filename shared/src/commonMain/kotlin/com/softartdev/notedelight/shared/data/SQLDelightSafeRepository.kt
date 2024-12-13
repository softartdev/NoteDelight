package com.softartdev.notedelight.shared.data

import com.softartdev.notedelight.shared.db.DatabaseHolder
import com.softartdev.notedelight.shared.db.NoteDAO
import com.softartdev.notedelight.shared.db.PlatformSQLiteThrowable
import com.softartdev.notedelight.shared.db.SafeRepo
import com.softartdev.notedelight.shared.PlatformSQLiteState

class SQLDelightSafeRepository(
    private val databaseHolder: DatabaseHolder,
    override val noteDAO: NoteDAO
) : SafeRepo() {

    override val databaseState: PlatformSQLiteState
        get() = databaseHolder.databaseState

    override val dbPath: String
        get() = databaseHolder.dbPath

    override fun buildDbIfNeed(passphrase: CharSequence): DatabaseHolder {
        return databaseHolder.buildDbIfNeed(passphrase)
    }

    override fun decrypt(oldPass: CharSequence) {
        try {
            databaseHolder.decrypt(oldPass)
        } catch (throwable: Throwable) {
            throw PlatformSQLiteThrowable(throwable.message.orEmpty())
        }
    }

    override fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        try {
            databaseHolder.rekey(oldPass, newPass)
        } catch (throwable: Throwable) {
            throw PlatformSQLiteThrowable(throwable.message.orEmpty())
        }
    }

    override fun encrypt(newPass: CharSequence) {
        try {
            databaseHolder.encrypt(newPass)
        } catch (throwable: Throwable) {
            throw PlatformSQLiteThrowable(throwable.message.orEmpty())
        }
    }

    override fun closeDatabase() {
        databaseHolder.closeDatabase()
    }
}
