package com.softartdev.notedelight.shared.database

import com.softartdev.notedelight.shared.db.NoteDb
import com.softartdev.notedelight.shared.db.NoteQueries
import app.cash.sqldelight.db.SqlDriver


abstract class DatabaseHolder {
    abstract val driver: SqlDriver
    abstract val noteDb: NoteDb
    abstract val noteQueries: NoteQueries

    abstract fun close()
}
