package com.softartdev.notedelight

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.createDefaultWebWorkerDriver
import com.softartdev.notedelight.db.NoteDb
import com.softartdev.notedelight.db.TestSchema
import com.softartdev.notedelight.db.createQueryWrapper
import com.softartdev.notedelight.db.toModel
import com.softartdev.notedelight.shared.db.NoteQueries
import io.github.aakira.napier.Napier
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SampleTest {

    @BeforeTest
    fun setUp() = Napier.base(PrintAntilog())

    @AfterTest
    fun tearDown() = Napier.takeLogarithm()

    @Test
    fun db() = runTest {
        val driver: SqlDriver = createDefaultWebWorkerDriver()
        Napier.d("Driver: $driver")
        NoteDb.Schema.awaitCreate(driver)
        Napier.d("Database schema created")
        val noteDb: NoteDb = createQueryWrapper(driver)
        Napier.d("NoteDb: $noteDb")
        val noteQueries: NoteQueries = noteDb.noteQueries
        Napier.d("NoteQueries: $noteQueries")
        var notes = noteQueries.getAll().awaitAsList().toModel()
        Napier.d("Notes: $notes")
        assertTrue(notes.isEmpty())
        Napier.d("Inserting test notes...")
        TestSchema.insertTestNotes(noteQueries)
        Napier.d("Test notes inserted")
        notes = noteQueries.getAll().awaitAsList().toModel()
        Napier.d("Notes after insert: $notes")
        assertContentEquals(TestSchema.notes, notes)
    }

    @Test
    fun sample() {
        assertEquals(expected = 4, actual = 2 + 2)
    }

    @Test
    fun sample2() {
        assertTrue(true)
    }
}