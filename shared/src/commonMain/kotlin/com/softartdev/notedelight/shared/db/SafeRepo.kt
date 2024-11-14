package com.softartdev.notedelight.shared.db

import com.softartdev.notedelight.shared.PlatformSQLiteState

abstract class SafeRepo {

    abstract val databaseState: PlatformSQLiteState

    abstract val noteDAO: NoteDAO

    abstract val dbPath: String

    var relaunchListFlowCallback: (() -> Any)? = null

    abstract fun buildDbIfNeed(passphrase: CharSequence = ""): DatabaseHolder

    abstract fun decrypt(oldPass: CharSequence)

    abstract fun rekey(oldPass: CharSequence, newPass: CharSequence)

    abstract fun encrypt(newPass: CharSequence)

    abstract fun closeDatabase()

    companion object {
        const val DB_NAME = "notes.db"
    }
}