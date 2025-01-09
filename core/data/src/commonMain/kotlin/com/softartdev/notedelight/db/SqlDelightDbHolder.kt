package com.softartdev.notedelight.db

import app.cash.sqldelight.db.SqlDriver
import com.softartdev.notedelight.shared.db.NoteQueries

interface SqlDelightDbHolder : DatabaseHolder {
    val driver: SqlDriver
    val noteDb: NoteDb
    val noteQueries: NoteQueries
}
