package com.softartdev.notedelight.shared.db

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class DriverFactory(
    private val applicationContext: Context
) {
    actual fun createDriver(): SqlDriver = AndroidSqliteDriver(
        schema = NoteDb.Schema,
        context = applicationContext,
        name = NOTE_DB_SQLDELIGHT_NAME
    )
}
