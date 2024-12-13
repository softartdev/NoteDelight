package com.softartdev.notedelight.shared.domain.repository

import com.softartdev.notedelight.shared.domain.model.DatabaseState
import com.softartdev.notedelight.shared.domain.model.Note

interface SafeRepository {

    val databaseState: DatabaseState

    val noteDAO: NoteDAO

    val dbPath: String

    var relaunchListFlowCallback: (() -> Any)?

    fun buildDbIfNeed(passphrase: CharSequence = ""): DatabaseHolder

    fun decrypt(oldPass: CharSequence)

    fun rekey(oldPass: CharSequence, newPass: CharSequence)

    fun encrypt(newPass: CharSequence)

    fun closeDatabase()

    companion object {
        const val DB_NAME = "notes.db"
    }
}
