package com.softartdev.notedelight.db

import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.db.SqlDriver
import com.softartdev.notedelight.shared.db.NoteQueries
import io.github.aakira.napier.Napier

interface SqlDelightDbHolder : DatabaseHolder {
    val driver: SqlDriver
    val noteDb: NoteDb
    val noteQueries: NoteQueries

    suspend fun createSchema() {
        try {
            Napier.d("Creating database schema")
            NoteDb.Schema.awaitCreate(driver)
            Napier.d("Database schema created")
        } catch (t: Throwable) {
            Napier.e("Error creating database schema", t)
        }
    }
}
