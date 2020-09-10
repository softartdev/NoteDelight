package com.softartdev.notedelight.shared.database

import com.softartdev.notedelight.shared.db.NoteQueries

abstract class DatabaseRepo {

    abstract val databaseState: PlatformSQLiteState

    abstract val noteQueries: NoteQueries

    var relaunchFlowEmitter: (() -> Unit)? = null

    abstract fun buildDatabaseInstanceIfNeed(passphrase: CharSequence = ""): DatabaseHolder

    abstract fun decrypt(oldPass: CharSequence)

    abstract fun rekey(oldPass: CharSequence, newPass: CharSequence)

    abstract fun encrypt(newPass: CharSequence)

    abstract fun closeDatabase()

    companion object {
        const val DB_NAME = "notes.db"

        fun copyCharArray(input: CharArray): CharArray = CharArray(input.size, input::get)
    }
}