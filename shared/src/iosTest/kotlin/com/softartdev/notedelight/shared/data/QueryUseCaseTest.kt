package com.softartdev.notedelight.shared.data

import com.softartdev.notedelight.shared.BaseTest
import com.softartdev.notedelight.shared.database.TestSchema
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.native.concurrent.AtomicInt
import kotlin.native.concurrent.InvalidMutabilityException
import kotlin.native.concurrent.isFrozen
import kotlin.test.*

class QueryUseCaseTest : BaseTest() {

    private val queryUseCase = QueryUseCase(dbRepo.noteQueries)

    private val notes = listOf(TestSchema.firstNote, TestSchema.secondNote, TestSchema.thirdNote)

    @BeforeTest
    fun setUp() {
        val noteDb = dbRepo.buildDatabaseInstanceIfNeed().noteDb
        noteDb.noteQueries.transaction {
            notes.forEach(noteDb.noteQueries::insert)
        }
    }

    @AfterTest
    fun tearDown() {
        val noteDb = dbRepo.buildDatabaseInstanceIfNeed().noteDb
        noteDb.noteQueries.deleteAll()
    }

    @Test
    fun launchInvalidMutabilityException() {
        val count = AtomicInt(0)
        runBlocking {
            withContext(Dispatchers.Default) {
                queryUseCase.noteQueries.getAll().asFlow().mapToList()
                    .onEach { actNotes ->
                        withContext(Dispatchers.Main) {
                            println("launchFlow - #${count.addAndGet(1)} isFrozen = ${actNotes.isFrozen}")
                            println("launchFlow - #${count.value} onEach = $actNotes")
                            if (actNotes.isNotEmpty()) {
                                println("launchFlow - #${count.value} assertEquals")
                                assertEquals(notes, actNotes)
                            }
                        }
                    }.catch {
                        withContext(Dispatchers.Main) {
                            it.printStackTrace()
                            assertFailsWith(InvalidMutabilityException::class) { throw it }
                        }
                    }.launchIn(this@withContext)
            }
        }
    }

    @Ignore
    @Test
    fun launchFlow() {
        val count = AtomicInt(0)
        runBlocking {
            withContext(Dispatchers.Default) {
                queryUseCase.noteQueries.getAll().asFlow().mapToList()
                    .onEach { actNotes ->
                        withContext(Dispatchers.Main) {
                            println("launchFlow - #${count.addAndGet(1)} isFrozen = ${actNotes.isFrozen}")
                            println("launchFlow - #${count.value} onEach = $actNotes")
                            if (actNotes.isNotEmpty()) {
                                println("launchFlow - #${count.value} assertEquals")
                                assertEquals(notes, actNotes)
                            }
                        }
                    }.catch {
                        withContext(Dispatchers.Main) {
                            throw it
                        }
                    }.launchIn(this@withContext)
            }
        }
    }

    @Test
    fun launchNotes() {
        val count = AtomicInt(0)
        queryUseCase.launchNotes(onSuccess = { actNotes ->
            println("launchNotes - #${count.addAndGet(1)} onSuccess = $actNotes")
            if (actNotes.isNotEmpty()) {
                println("launchNotes - #${count.value} assertEquals")
                assertEquals(notes, actNotes)
            }
        }, onFailure = { throwable -> throw throwable })
    }

    @Test
    fun getNotes() {
        val actual = runBlocking { queryUseCase.noteQueries.getAll().asFlow().mapToList().first() }
        assertEquals(notes, actual)
    }

    @Test
    fun saveNote() {
        val id: Long = 2
        val newTitle = "new title"
        val newText = "new text"
        val expSavedNote = TestSchema.secondNote.copy(title = newTitle, text = newText)
        val actSavedNote = runBlocking { queryUseCase.saveNote(id, newTitle, newText) }
        assertEquals(expSavedNote.title, actSavedNote.title)
        assertEquals(expSavedNote.text, actSavedNote.text)
        val updatedNote = runBlocking { queryUseCase.loadNote(id) }
        assertEquals(newTitle, updatedNote.title)
        assertEquals(newText, updatedNote.text)
    }

    @Test
    fun loadNote() {
        val id: Long = 2
        val exp = notes.find { it.id == id }
        val act = runBlocking { queryUseCase.loadNote(id) }
        assertEquals(exp, act)
    }

}