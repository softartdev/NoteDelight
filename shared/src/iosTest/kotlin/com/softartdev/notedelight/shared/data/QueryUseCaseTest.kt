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
import kotlin.native.concurrent.isFrozen
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class QueryUseCaseTest : BaseTest() {
    
    private val queryUseCase = QueryUseCase(dbRepo.noteQueries)

    private val notes = listOf(TestSchema.firstNote, TestSchema.secondNote, TestSchema.thirdNote)

    @BeforeTest
    fun setUp() {
        val noteDb = dbRepo.buildDatabaseInstanceIfNeed().noteDb
        notes.forEach(noteDb.noteQueries::insert)
    }

    @AfterTest
    fun tearDown() {
        val noteDb = dbRepo.buildDatabaseInstanceIfNeed().noteDb
        noteDb.noteQueries.deleteAll()
    }

    @Test
    fun launchNotesFlow() {
        val count = AtomicInt(0)
        val flow = queryUseCase.noteQueries.getAll().asFlow().mapToList()
        runBlocking {
            withContext(Dispatchers.Default) {
                flow.onEach { actNotes ->
                    withContext(this@runBlocking.coroutineContext) {
                        println("launchNotes - #${count.addAndGet(1)} isFrozen = ${actNotes.isFrozen}")
                        println("launchNotes - #${count.value} onEach = $actNotes")
                        if (actNotes.isNotEmpty()) {
                            println("launchNotes - #${count.value} assertEquals")
                            assertEquals(notes, actNotes)
                        }
                    }
                }.catch { throwable ->
                    withContext(this@runBlocking.coroutineContext) {
                        throw Throwable("catch flow error: ${throwable.message}", throwable)
                    }
                }.launchIn(this@withContext)
            }.join()
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