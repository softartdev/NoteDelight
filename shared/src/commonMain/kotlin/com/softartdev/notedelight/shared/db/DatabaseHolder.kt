package com.softartdev.notedelight.shared.db

import app.cash.sqldelight.db.SqlDriver

abstract class DatabaseHolder {
    abstract val driver: SqlDriver
    abstract val noteDb: NoteDb
    abstract val noteQueries: NoteQueries

    abstract fun close()
}