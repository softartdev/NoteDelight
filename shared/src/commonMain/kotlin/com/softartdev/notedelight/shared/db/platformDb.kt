package com.softartdev.notedelight.shared.db

import com.squareup.sqldelight.db.SqlDriver

const val NOTE_DB_SQLDELIGHT_NAME = "note.db"

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): NoteDb {
    val driver = driverFactory.createDriver()
    val dateAdapter = DateAdapter()
    val noteAdapter = Note.Adapter(dateAdapter, dateAdapter)
    return NoteDb(driver, noteAdapter)
}
