package com.softartdev.notedelight.shared.data

import com.softartdev.notedelight.shared.database.AndroidDbRepo
import com.softartdev.notedelight.shared.database.TestSchema
import com.softartdev.notedelight.shared.database.createQueryWrapper
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.db.NoteDb
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class NoteUseCaseUnitTest {

    private val mockDbRepo = Mockito.mock(AndroidDbRepo::class.java)
    private val noteUseCase = NoteUseCase(mockDbRepo)

    private val noteDb = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).let { driver ->
        NoteDb.Schema.create(driver)
        return@let createQueryWrapper(driver)
    }
    private val notes: List<Note> = listOf(TestSchema.firstNote, TestSchema.secondNote, TestSchema.thirdNote)

    @Before
    fun setUp() = runBlocking<Unit> {
        notes.forEach(noteDb.noteQueries::insert)
        Mockito.`when`(mockDbRepo.noteQueries).thenReturn(noteDb.noteQueries)
    }

    @After
    fun tearDown() = runBlocking {
        noteDb.noteQueries.deleteAll()
    }

    @Test
    fun getTitleChannel() = runBlocking {
        val act = "test title"
        val deferred = async { noteUseCase.titleChannel.receive() }
        noteUseCase.titleChannel.send(act)
        val exp = deferred.await()
        assertEquals(exp, act)
    }

    @Test
    fun getNotes() = runBlocking {
        assertEquals(notes, noteUseCase.getNotes().first())
    }

    @Test
    fun createNote() = runBlocking {
        assertEquals(notes.last().id.inc(), noteUseCase.createNote())
    }

    @Test
    fun saveNote() = runBlocking {
        val id: Long = 2
        val newTitle = "new title"
        val newText = "new text"
        noteUseCase.saveNote(id, newTitle, newText)
        val updatedNote = noteUseCase.loadNote(id)
        assertEquals(newTitle, updatedNote.title)
        assertEquals(newText, updatedNote.text)
    }

    @Test
    fun updateTitle() = runBlocking {
        val id: Long = 2
        val newTitle = "new title"
        assertEquals(1, noteUseCase.updateTitle(id, newTitle))
        val updatedNote = noteUseCase.loadNote(id)
        assertEquals(newTitle, updatedNote.title)
    }

    @Test
    fun loadNote() = runBlocking {
        val id: Long = 2
        val exp = notes.find { it.id == id }
        val act = noteUseCase.loadNote(id)
        assertEquals(exp, act)
    }

    @Test(expected = NullPointerException::class)
    fun deleteNote() = runBlocking<Unit> {
        val id: Long = 2
        assertEquals(1, noteUseCase.deleteNote(id))
        noteUseCase.loadNote(id)
    }

    @Test
    fun isChanged() = runBlocking {
        val note = notes.random()
        assertFalse(noteUseCase.isChanged(note.id, note.title, note.text))
        assertTrue(noteUseCase.isChanged(note.id, "new title", "new text"))
    }

    @Test
    fun isEmpty() = runBlocking {
        val note = notes.random()
        assertFalse(noteUseCase.isEmpty(note.id))
    }
}