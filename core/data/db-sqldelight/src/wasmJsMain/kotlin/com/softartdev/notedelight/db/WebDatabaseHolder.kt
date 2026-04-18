@file:OptIn(ExperimentalWasmJsInterop::class)

package com.softartdev.notedelight.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.shared.db.NoteQueries
import org.w3c.dom.Worker

class WebDatabaseHolder(private val key: String? = null) : SqlDelightDbHolder {
    override val logger = Logger.withTag("WebDatabaseHolder")
    override val driver: SqlDriver = WebWorkerDriver(worker = jsWorker())
    override val noteDb: NoteDb = createQueryWrapper(driver)
    override val noteQueries: NoteQueries = noteDb.noteQueries

    /**
     * Configures SQLCipher v4 compatibility and sets the encryption key.
     * Must be called BEFORE any other SQL operations (including createSchema).
     *
     * Uses `cipher=sqlcipher` with `legacy=4` to match the Desktop JVM and Android
     * encryption format, enabling cross-platform encrypted backup portability.
     */
    suspend fun applyKey() {
        if (!key.isNullOrEmpty()) {
            val escapedKey = key.replace("'", "''")
            logger.d { "Configuring SQLCipher v4 and setting encryption key" }
            driver.execute(null, "PRAGMA cipher = 'sqlcipher'", 0, null).await()
            driver.execute(null, "PRAGMA legacy = 4", 0, null).await()
            driver.execute(null, "PRAGMA key = '$escapedKey'", 0, null).await()
        }
    }

    override fun close() = driver.close()
}

// Create worker with custom OPFS-enabled script
private fun jsWorker(): Worker = js("new Worker(new URL('sqlite.worker.js', import.meta.url))")
