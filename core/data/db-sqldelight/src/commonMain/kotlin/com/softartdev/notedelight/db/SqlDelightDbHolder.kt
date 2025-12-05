package com.softartdev.notedelight.db

import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.db.SqlDriver
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.shared.db.NoteQueries

interface SqlDelightDbHolder : DatabaseHolder {
    val driver: SqlDriver
    val noteDb: NoteDb
    val noteQueries: NoteQueries

    suspend fun createSchema() {
        try {
            Logger.d(LOG_TAG) { "Creating database schema" }
            NoteDb.Schema.awaitCreate(driver)
            Logger.d(LOG_TAG) { "Database schema created" }
        } catch (t: Throwable) {
            Logger.e(t, LOG_TAG) { "Error creating database schema" }
        }
    }

    companion object {
        private const val LOG_TAG: String = "SqlDelightDbHolder"
    }
}
