package com.softartdev.notedelight.shared.db

import kotlinx.coroutines.flow.first
import kotlin.test.*

abstract class BasePlatformNoteUseCaseTest {

    abstract val platformRepo: PlatformRepo
    private val noteUseCase = PlatformNoteUseCase(platformRepo)

    private val notes: MutableList<Note> = mutableListOf()

    @BeforeTest
    fun setUp() {
        platformRepo.buildDatabaseInstanceIfNeed()

        val firstNote = Note(1, "first title", "first text", Date(), Date())
        val secondNote = Note(2, "second title", "second text", Date(), Date())
        val thirdNote = Note(3, "third title", "third text", Date(), Date())
        notes.addAll(listOf(firstNote, secondNote, thirdNote))
    }

    @AfterTest
    fun tearDown() {
        platformRepo.closeDatabase()

        notes.clear()
    }

    internal abstract fun <T> runTest(block: suspend () -> T)

    @Test
    fun getTitleChannel() {
        assertNotNull(noteUseCase.titleChannel)
    }

    @Test
    fun getNotes() = runTest {
        assertEquals(notes, noteUseCase.getNotes().first())
    }

    @Test
    fun createNote() = runTest {
        assertEquals(notes.last().id.inc(), noteUseCase.createNote())
    }

    @Test
    fun saveNote() = runTest {
        val id: Long = 2
        val newTitle = "new title"
        val newText = "new text"
        noteUseCase.saveNote(id, newTitle, newText)
        val updatedNote = notes.find { it.id == id }
        assertEquals(newTitle, updatedNote?.title)
        assertEquals(newText, updatedNote?.text)
    }

    @Test
    fun updateTitle() = runTest {
        val id: Long = 2
        val newTitle = "new title"
        noteUseCase.updateTitle(id, newTitle)
        val updatedNote = notes.find { it.id == id }
        assertEquals(newTitle, updatedNote?.title)
    }

    @Test
    fun loadNote() = runTest {
        val id: Long = 2
        val exp = notes.find { it.id == id }
        val act = noteUseCase.loadNote(id)
        assertEquals(exp, act)
    }

    @Test
    fun deleteNote() = runTest {
        val id: Long = 2
        assertNotNull(notes.find { it.id == id })
        noteUseCase.deleteNote(id)
        assertNull(notes.find { it.id == id })
    }

    @Test
    fun isChanged() = runTest {
        val note = notes.random()
        assertFalse(noteUseCase.isChanged(note.id, note.title, note.text))
        assertTrue(noteUseCase.isChanged(note.id, "new title", "new text"))
    }

    @Test
    fun isEmpty() = runTest {
        val note = notes.random()
        assertFalse(noteUseCase.isEmpty(note.id))
    }
}