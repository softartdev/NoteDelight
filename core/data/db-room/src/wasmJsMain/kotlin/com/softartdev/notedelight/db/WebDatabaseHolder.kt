@file:OptIn(ExperimentalWasmJsInterop::class)

package com.softartdev.notedelight.db

import androidx.room3.Room
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import com.softartdev.notedelight.repository.SafeRepo
import kotlinx.coroutines.await
import org.w3c.dom.Worker
import kotlin.js.Promise

class WebDatabaseHolder(key: String? = null) : RoomDbHolder {
    internal val worker: Worker = createWorker()

    val noteDatabase: NoteDatabase = Room
        .inMemoryDatabaseBuilder<NoteDatabase>()
        .setDriver(
            NamedWebWorkerSQLiteDriver(
                delegate = WebWorkerSQLiteDriver(worker),
                databaseName = workerDatabaseName(key),
            ),
        )
        .addMigrations(NOTE_DATABASE_MIGRATION_1_2)
        .build()

    override fun close() = worker.terminate()

    internal suspend fun shutdown() {
        // RoomDatabase.close() waits synchronously for its connection pool, blocking the browser
        // thread that must deliver worker messages. Completed DAO calls have already received their
        // commit response, so terminating this holder's worker safely releases its OPFS handle.
        close()
        awaitNextTask()
    }
}

/**
 * Room uses a single-connection pool for its in-memory builder. The worker maps that logical
 * connection to the named OPFS database, so data remains persistent without concurrent OPFS opens.
 */
private class NamedWebWorkerSQLiteDriver(
    private val delegate: WebWorkerSQLiteDriver,
    private val databaseName: String,
) : SQLiteDriver {
    override suspend fun open(fileName: String): SQLiteConnection = delegate.open(databaseName)
}

private fun workerDatabaseName(key: String?): String = if (key.isNullOrEmpty()) {
    SafeRepo.DB_NAME
} else {
    "${SafeRepo.DB_NAME}?key=${encodeUriComponent(key)}"
}

@JsFun("(value) => encodeURIComponent(value)")
private external fun encodeUriComponent(value: String): String

internal fun jsWorker(): Worker = createWorker()

private fun createWorker(): Worker = js("new Worker('room3.worker.js')")

internal suspend fun awaitWorkerIdle(worker: Worker) {
    workerIdlePromise(worker).await<JsAny?>()
    awaitNextTask()
}

internal suspend fun terminateWorker(worker: Worker) {
    worker.terminate()
    awaitNextTask()
}

private suspend fun awaitNextTask() {
    nextTaskPromise().await<JsAny?>()
}

@JsFun(
    """
    (worker) => new Promise((resolve, reject) => {
      const id = -1;
      worker.onmessage = (event) => {
        if (event.data && event.data.id === id) {
          if (event.data.error) reject(new Error(event.data.error));
          else resolve(null);
        }
      };
      worker.onerror = (event) => reject(new Error(event.message || 'Room worker error'));
      worker.postMessage({ id, data: { cmd: 'ping' } });
    })
    """,
)
private external fun workerIdlePromise(worker: Worker): Promise<JsAny?>

@JsFun("() => new Promise((resolve) => setTimeout(() => resolve(null), 0))")
private external fun nextTaskPromise(): Promise<JsAny?>
