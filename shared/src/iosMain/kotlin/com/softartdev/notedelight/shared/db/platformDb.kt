package com.softartdev.notedelight.shared.db

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class DriverFactory {
    actual fun createDriver(): SqlDriver = NativeSqliteDriver(
        schema = NoteDb.Schema,
        name = NOTE_DB_SQLDELIGHT_NAME
    )
}
