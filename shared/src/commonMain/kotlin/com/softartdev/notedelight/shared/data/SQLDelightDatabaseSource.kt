package com.softartdev.notedelight.shared.data

import com.softartdev.notedelight.shared.db.DatabaseHolder
import com.softartdev.notedelight.shared.db.NoteDb
import com.softartdev.notedelight.shared.db.NoteQueries

class SQLDelightDatabaseSource(
    private val databaseHolder: DatabaseHolder
) {
    val noteDb: NoteDb
        get() = databaseHolder.noteDb

    val noteQueries: NoteQueries
        get() = databaseHolder.noteQueries

    fun close() {
        databaseHolder.close()
    }
}
