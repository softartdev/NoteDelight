package com.softartdev.notedelight.db

import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.db.SqlDriver
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.shared.db.NoteQueries

interface SqlDelightDbHolder : DatabaseHolder {
    val logger: Logger
    val driver: SqlDriver
    val noteDb: NoteDb
    val noteQueries: NoteQueries

    suspend fun createSchema() = try {
        logger.d { "Creating database schema" }
        NoteDb.Schema.awaitCreate(driver)
        logger.d { "Database schema created" }
    } catch (t: Throwable) {
        logger.e(t) { "Error creating database schema" }
    }
}
