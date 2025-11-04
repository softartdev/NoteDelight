@file:OptIn(ExperimentalWasmJsInterop::class)

package com.softartdev.notedelight.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import com.softartdev.notedelight.shared.db.NoteQueries
import org.w3c.dom.Worker

class WebDatabaseHolder : SqlDelightDbHolder {
    override val driver: SqlDriver = WebWorkerDriver(worker = jsWorker())
    override val noteDb: NoteDb = createQueryWrapper(driver)
    override val noteQueries: NoteQueries = noteDb.noteQueries

    override fun close() = driver.close()
}

// Create worker with custom OPFS-enabled script
private fun jsWorker(): Worker = js("new Worker(new URL('sqlite.worker.js', import.meta.url))")