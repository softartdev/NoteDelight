package com.softartdev.notedelight.repository

import com.softartdev.notedelight.db.DatabaseHolder
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.model.PlatformSQLiteState

abstract class SafeRepo {

    abstract val databaseState: PlatformSQLiteState

    abstract val noteDAO: NoteDAO

    abstract val dbPath: String

    var relaunchListFlowCallback: (() -> Any)? = null

    abstract suspend fun buildDbIfNeed(passphrase: CharSequence = ""): DatabaseHolder

    abstract suspend fun decrypt(oldPass: CharSequence)

    abstract suspend fun rekey(oldPass: CharSequence, newPass: CharSequence)

    abstract suspend fun encrypt(newPass: CharSequence)

    abstract suspend fun execute(query: String): String?

    abstract suspend fun closeDatabase()

    open suspend fun deleteDatabase(): Boolean = false

    companion object {
        const val DB_NAME = "notes.db"
    }
}
