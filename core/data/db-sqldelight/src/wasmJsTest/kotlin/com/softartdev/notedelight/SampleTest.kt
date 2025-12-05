package com.softartdev.notedelight

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.createDefaultWebWorkerDriver
import co.touchlab.kermit.CommonWriter
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.db.NoteDb
import com.softartdev.notedelight.db.TestSchema
import com.softartdev.notedelight.db.createQueryWrapper
import com.softartdev.notedelight.db.toModel
import com.softartdev.notedelight.shared.db.NoteQueries
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SampleTest {
    private val logger = Logger.withTag(this@SampleTest::class.simpleName.toString())

    @BeforeTest
    fun setUp() = Logger.setLogWriters(CommonWriter())

    @AfterTest
    fun tearDown() = Logger.setLogWriters()

    @Test
    fun db() = runTest {
        val driver: SqlDriver = createDefaultWebWorkerDriver()
        logger.d { "Driver: $driver" }
        NoteDb.Schema.awaitCreate(driver)
        logger.d { "Database schema created" }
        val noteDb: NoteDb = createQueryWrapper(driver)
        logger.d { "NoteDb: $noteDb" }
        val noteQueries: NoteQueries = noteDb.noteQueries
        logger.d { "NoteQueries: $noteQueries" }
        var notes = noteQueries.getAll().awaitAsList().toModel()
        logger.d { "Notes: $notes" }
        assertTrue(notes.isEmpty())
        logger.d { "Inserting test notes..." }
        TestSchema.insertTestNotes(noteQueries)
        logger.d { "Test notes inserted" }
        notes = noteQueries.getAll().awaitAsList().toModel()
        logger.d { "Notes after insert: $notes" }
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