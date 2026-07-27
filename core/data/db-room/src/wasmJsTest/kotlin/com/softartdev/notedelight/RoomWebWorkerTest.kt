@file:OptIn(ExperimentalWasmJsInterop::class, kotlinx.coroutines.DelicateCoroutinesApi::class)

package com.softartdev.notedelight

import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import com.softartdev.notedelight.db.awaitWorkerIdle
import com.softartdev.notedelight.db.jsWorker
import com.softartdev.notedelight.db.terminateWorker
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.repository.WebSafeRepo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlinx.datetime.LocalDateTime
import kotlin.js.Promise
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RoomWebWorkerTest {
    @Test
    fun workerProtocol(): Promise<JsAny?> = GlobalScope.promise {
        val worker = jsWorker()
        val connection = WebWorkerSQLiteDriver(worker).open(":memory:")
        try {
            connection.prepare("CREATE TABLE worker_test (value INTEGER NOT NULL)").use { it.step() }
            connection.prepare("INSERT INTO worker_test VALUES (42)").use { it.step() }
            connection.prepare("SELECT value FROM worker_test").use { statement ->
                assertEquals(true, statement.step())
                assertEquals(42, statement.getLong(0))
            }
        } finally {
            connection.close()
            awaitWorkerIdle(worker)
            terminateWorker(worker)
        }
        null
    }

    @Test
    fun crudAndPersistence(): Promise<JsAny?> = GlobalScope.promise {
        val safeRepo = WebSafeRepo()
        safeRepo.buildDbIfNeed()
        safeRepo.noteDAO.deleteAll()
        safeRepo.noteDAO.insert(testNote())
        assertEquals("wasm", safeRepo.noteDAO.load(1).title)
        safeRepo.closeDatabase()

        safeRepo.buildDbIfNeed()
        assertEquals("wasm", safeRepo.noteDAO.load(1).title)
        safeRepo.noteDAO.deleteAll()
        safeRepo.closeDatabase()
        null
    }

    @Test
    fun encryptionLifecyclePreservesNotes(): Promise<JsAny?> = GlobalScope.promise {
        val firstPassword = "first password"
        val secondPassword = "second password"
        val safeRepo = WebSafeRepo()
        safeRepo.buildDbIfNeed()
        safeRepo.noteDAO.deleteAll()
        safeRepo.noteDAO.insert(testNote())

        safeRepo.encrypt(firstPassword)
        assertEquals("wasm", safeRepo.noteDAO.load(1).title)
        safeRepo.closeDatabase()

        val wrongPasswordRepo = WebSafeRepo()
        var wrongPasswordRejected = false
        try {
            wrongPasswordRepo.buildDbIfNeed("wrong password")
        } catch (_: Throwable) {
            wrongPasswordRejected = true
        } finally {
            wrongPasswordRepo.closeDatabase()
        }
        assertTrue(wrongPasswordRejected)

        safeRepo.buildDbIfNeed(firstPassword)
        safeRepo.rekey(firstPassword, secondPassword)
        assertEquals("wasm", safeRepo.noteDAO.load(1).title)
        safeRepo.decrypt(secondPassword)
        assertEquals("wasm", safeRepo.noteDAO.load(1).title)
        safeRepo.noteDAO.deleteAll()
        safeRepo.closeDatabase()
        null
    }

    private fun testNote(): Note = Note(
        id = 1,
        title = "wasm",
        text = "Room 3 worker",
        dateCreated = LocalDateTime(2026, 1, 2, 3, 4),
        dateModified = LocalDateTime(2026, 1, 2, 3, 4),
    )
}
