@file:OptIn(ExperimentalWasmJsInterop::class)

package com.softartdev.notedelight.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.shared.db.NoteQueries
import org.w3c.dom.Worker

class WebDatabaseHolder(private val key: String? = null) : SqlDelightDbHolder {
    private val logger = Logger.withTag("WebDatabaseHolder")
    override val driver: SqlDriver = WebWorkerDriver(worker = jsWorker())
    override val noteDb: NoteDb = createQueryWrapper(driver)
    override val noteQueries: NoteQueries = noteDb.noteQueries

    /**
     * Sets the encryption key on the database connection.
     * Must be called BEFORE any other SQL operations (including createSchema).
     * This sends PRAGMA key as the first statement to the worker.
     */
    suspend fun applyKey() {
        if (!key.isNullOrEmpty()) {
            val escapedKey = key.replace("'", "''")
            logger.d { "Setting encryption key on database" }
            driver.execute(null, "PRAGMA key = '$escapedKey'", 0, null).await()
        }
    }

    override fun close() = driver.close()
}

// Create worker with custom OPFS-enabled script
private fun jsWorker(): Worker = js("new Worker(new URL('sqlite.worker.js', import.meta.url))")
