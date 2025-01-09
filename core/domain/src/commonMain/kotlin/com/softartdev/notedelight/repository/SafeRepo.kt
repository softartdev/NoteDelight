package com.softartdev.notedelight.repository

import com.softartdev.notedelight.db.DatabaseHolder
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.model.PlatformSQLiteState

abstract class SafeRepo {

    abstract val databaseState: PlatformSQLiteState

    abstract val noteDAO: NoteDAO

    abstract val dbPath: String

    var relaunchListFlowCallback: (() -> Any)? = null

    abstract fun buildDbIfNeed(passphrase: CharSequence = ""): DatabaseHolder

    abstract fun decrypt(oldPass: CharSequence)

    abstract fun rekey(oldPass: CharSequence, newPass: CharSequence)

    abstract fun encrypt(newPass: CharSequence)

    abstract fun execute(query: String): String?

    abstract fun closeDatabase()

    companion object {
        const val DB_NAME = "notes.db"
    }
}