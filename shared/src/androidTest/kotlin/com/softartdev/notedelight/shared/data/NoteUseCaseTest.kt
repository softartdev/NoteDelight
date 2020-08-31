package com.softartdev.notedelight.shared.data

import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.database.NoteDao
import com.softartdev.notedelight.shared.date.createLocalDateTime
import com.softartdev.notedelight.shared.test.util.MainCoroutineRule
import com.softartdev.notedelight.shared.test.util.anyObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class NoteUseCaseTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val mockSafeRepo = Mockito.mock(SafeRepo::class.java)
    private val noteUseCase = NoteUseCase(mockSafeRepo)

    private val notes: MutableList<Note> = mutableListOf()

    @Before
    fun setUp() = mainCoroutineRule.runBlockingTest {
        val ldt = createLocalDateTime()
        val firstNote = Note(1, "first title", "first text", ldt, ldt)
        val secondNote = Note(2, "second title", "second text", ldt, ldt)
        val thirdNote = Note(3, "third title", "third text", ldt, ldt)
        notes.addAll(listOf(firstNote, secondNote, thirdNote))
        val mockNoteDao = Mockito.mock(NoteDao::class.java)
        Mockito.`when`(mockSafeRepo.noteDao).thenReturn(mockNoteDao)
        Mockito.`when`(mockNoteDao.getNotes()).thenReturn(flowOf(notes))
        Mockito.`when`(mockNoteDao.insertNote(anyObject())).thenReturn(notes.last().id.inc())
        Mockito.`when`(mockNoteDao.getNoteById(Mockito.anyLong())).thenAnswer { invocationOnMock ->
            val id: Long = invocationOnMock.arguments[0] as Long
            return@thenAnswer notes.find { foundNote -> foundNote.id == id }
        }
        Mockito.`when`(mockNoteDao.updateNote(anyObject())).thenAnswer { invocationOnMock ->
            val note: Note = invocationOnMock.arguments[0] as Note
            val index = notes.indexOfFirst { foundNote -> foundNote.id == note.id }
            notes.removeAt(index)
            notes.add(index, note)
            return@thenAnswer 1
        }
        Mockito.`when`(mockNoteDao.deleteNoteById(Mockito.anyLong())).thenAnswer { invocationOnMock ->
            val id: Long = invocationOnMock.arguments[0] as Long
            notes.removeIf { foundNote -> foundNote.id == id }
            return@thenAnswer 1
        }
    }

    @After
    fun tearDown() {
        notes.clear()
    }

    @Test
    fun getTitleChannel() {
        assertNotNull(noteUseCase.titleChannel)
    }

    @Test
    fun getNotes() = mainCoroutineRule.runBlockingTest {
        assertEquals(notes, noteUseCase.getNotes().first())
    }

    @Test
    fun createNote() = mainCoroutineRule.runBlockingTest {
        assertEquals(notes.last().id.inc(), noteUseCase.createNote())
    }

    @Test
    fun saveNote() = mainCoroutineRule.runBlockingTest {
        val id: Long = 2
        val newTitle = "new title"
        val newText = "new text"
        assertEquals(1, noteUseCase.saveNote(id, newTitle, newText))
        val updatedNote = notes.find { it.id == id }
        assertEquals(newTitle, updatedNote?.title)
        assertEquals(newText, updatedNote?.text)
    }

    @Test
    fun updateTitle() = mainCoroutineRule.runBlockingTest {
        val id: Long = 2
        val newTitle = "new title"
        assertEquals(1, noteUseCase.updateTitle(id, newTitle))
        val updatedNote = notes.find { it.id == id }
        assertEquals(newTitle, updatedNote?.title)
    }

    @Test
    fun loadNote() = mainCoroutineRule.runBlockingTest {
        val id: Long = 2
        val exp = notes.find { it.id == id }
        val act = noteUseCase.loadNote(id)
        assertEquals(exp, act)
    }

    @Test
    fun deleteNote() = mainCoroutineRule.runBlockingTest {
        val id: Long = 2
        assertNotNull(notes.find { it.id == id })
        assertEquals(1, noteUseCase.deleteNote(id))
        assertNull(notes.find { it.id == id })
    }

    @Test
    fun isChanged() = mainCoroutineRule.runBlockingTest {
        val note = notes.random()
        assertFalse(noteUseCase.isChanged(note.id, note.title, note.text))
        assertTrue(noteUseCase.isChanged(note.id, "new title", "new text"))
    }

    @Test
    fun isEmpty() = mainCoroutineRule.runBlockingTest {
        val note = notes.random()
        assertFalse(noteUseCase.isEmpty(note.id))
    }
}