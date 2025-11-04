@file:OptIn(ExperimentalWasmJsInterop::class)

package com.softartdev.notedelight.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.createDefaultWebWorkerDriver
import com.softartdev.notedelight.shared.db.NoteQueries

/**
 * Test-specific WebDatabaseHolder that always uses the default SQLDelight worker.
 * This avoids webpack resolution issues with the custom OPFS worker script in test environments.
 */
class TestWebDatabaseHolder : SqlDelightDbHolder {
    override val driver: SqlDriver = createDefaultWebWorkerDriver()
    override val noteDb: NoteDb = createQueryWrapper(driver)
    override val noteQueries: NoteQueries = noteDb.noteQueries

    override fun close() = driver.close()
}
