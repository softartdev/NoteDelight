package com.softartdev.notedelight.shared.db

import kotlin.test.*

abstract class BasePlatformNoteUseCaseTest {

    abstract val platformRepo: PlatformRepo
    private lateinit var noteUseCase: PlatformNoteUseCase

    private val notes: MutableList<Note> = mutableListOf()

    @BeforeTest
    open fun setUp() {
        platformRepo.buildDatabaseInstanceIfNeed()
        noteUseCase = PlatformNoteUseCase(platformRepo)

        val firstNote = Note(1, "first title", "first text", Date(), Date())
        val secondNote = Note(2, "second title", "second text", Date(), Date())
        val thirdNote = Note(3, "third title", "third text", Date(), Date())
        notes.addAll(listOf(firstNote, secondNote, thirdNote))

        notes.forEach(platformRepo.noteQueries::insert)
    }

    @AfterTest
    open fun tearDown() {
        platformRepo.closeDatabase()

        notes.clear()
    }

    @Test
    fun getTitleChannel() {
        assertNotNull(noteUseCase.titleChannel)
    }

    @Test
    fun getNotes() {
        assertEquals(notes, platformRepo.noteQueries.getAll().executeAsList())
    }

    @Test
    fun createNote() {
        assertEquals(notes.first().id.dec(), noteUseCase.createNote())
    }

    @Test
    fun saveNote() {
        val id: Long = 2
        val newTitle = "new title"
        val newText = "new text"
        noteUseCase.saveNote(id, newTitle, newText)
        val updatedNote = noteUseCase.loadNote(id)
        assertEquals(newTitle, updatedNote.title)
        assertEquals(newText, updatedNote.text)
    }

    @Test
    fun updateTitle() {
        val id: Long = 2
        val newTitle = "new title"
        noteUseCase.updateTitle(id, newTitle)
        val updatedNote = noteUseCase.loadNote(id)
        assertEquals(newTitle, updatedNote.title)
    }

    @Test
    fun loadNote() {
        val id: Long = 2
        val exp = notes.find { it.id == id }
        val act = noteUseCase.loadNote(id)
        assertEquals(exp, act)
    }

    @Test
    fun deleteNote() {
        val id: Long = 2
        assertNotNull(platformRepo.noteQueries.getById(id).executeAsOneOrNull())
        noteUseCase.deleteNote(id)
        assertNull(platformRepo.noteQueries.getById(id).executeAsOneOrNull())
    }

    @Test
    fun isChanged() {
        val note = notes.random()
        assertFalse(noteUseCase.isChanged(note.id, note.title, note.text))
        assertTrue(noteUseCase.isChanged(note.id, "new title", "new text"))
    }

    @Test
    fun isEmpty() {
        val note = notes.random()
        assertFalse(noteUseCase.isEmpty(note.id))
    }
}