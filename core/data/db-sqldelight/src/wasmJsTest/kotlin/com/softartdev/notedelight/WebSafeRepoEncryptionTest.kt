@file:OptIn(
    ExperimentalWasmJsInterop::class,
    kotlinx.coroutines.DelicateCoroutinesApi::class,
)

package com.softartdev.notedelight

import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.repository.WebSafeRepo
import com.softartdev.notedelight.util.CoroutineDispatchersImpl
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlinx.datetime.LocalDateTime
import kotlin.js.Promise
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WebSafeRepoEncryptionTest {
    @Test
    fun encryptionLifecyclePreservesNotes(): Promise<JsAny?> = GlobalScope.promise {
        val firstPassword = "first password"
        val secondPassword = "second password"
        val safeRepo = WebSafeRepo(CoroutineDispatchersImpl())
        safeRepo.buildDbIfNeed()
        safeRepo.noteDAO.deleteAll()
        safeRepo.noteDAO.insert(testNote())

        safeRepo.encrypt(firstPassword)
        assertEquals("wasm", safeRepo.noteDAO.load(1).title)
        safeRepo.closeDatabase()

        val wrongPasswordRepo = WebSafeRepo(CoroutineDispatchersImpl())
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
        text = "SQLDelight worker",
        dateCreated = LocalDateTime(2026, 1, 2, 3, 4),
        dateModified = LocalDateTime(2026, 1, 2, 3, 4),
    )
}
