package com.softartdev.notedelight.shared.presentation.note

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.shared.data.NoteUseCase
import com.softartdev.notedelight.shared.date.createLocalDateTime
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.presentation.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class NoteViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val noteUseCase = Mockito.mock(NoteUseCase::class.java)
    private var noteViewModel = NoteViewModel(noteUseCase)

    private val id = 1L
    private val title: String = "title"
    private val text: String = "text"
    private val ldt: LocalDateTime = createLocalDateTime()
    private val note = Note(id, title, text, ldt, ldt)
    private val titleChannel = Channel<String>()

    @Before
    fun setUp() = runTest {
        Mockito.`when`(noteUseCase.createNote()).thenReturn(id)
        Mockito.`when`(noteUseCase.loadNote(id)).thenReturn(note)
        Mockito.`when`(noteUseCase.saveNote(id, title, text)).thenReturn(note)
        Mockito.`when`(noteUseCase.titleChannel).thenReturn(titleChannel)
        Mockito.`when`(noteUseCase.deleteNote(id)).thenReturn(1)
    }

    @After
    fun tearDown() = runTest {
        noteViewModel.resetLoadingResult()
    }

    @Test
    fun createNote() = runTest {
        noteViewModel.resultStateFlow.test {
            assertEquals(NoteResult.Loading, awaitItem())

            noteViewModel.createNote()
            assertEquals(NoteResult.Created(id), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadNote() = runTest {
        noteViewModel.resultStateFlow.test {
            assertEquals(NoteResult.Loading, awaitItem())

            noteViewModel.loadNote(id)
            assertEquals(NoteResult.Loaded(note), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun saveNoteEmpty() = runTest {
        noteViewModel.resultStateFlow.test {
            assertEquals(NoteResult.Loading, awaitItem())

            noteViewModel.saveNote("", "")
            assertEquals(NoteResult.Empty, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun saveNote() = runTest {
        noteViewModel.resultStateFlow.test {
            assertEquals(NoteResult.Loading, awaitItem())

            noteViewModel.setIdForTest(id)
            noteViewModel.saveNote(title, text)
            assertEquals(NoteResult.Saved(title), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun editTitle() = runTest {
        noteViewModel.resultStateFlow.test {
            assertEquals(NoteResult.Loading, awaitItem())

            noteViewModel.setIdForTest(id)
            noteViewModel.editTitle()
            assertEquals(NoteResult.NavEditTitle(id), awaitItem())

            titleChannel.send(title)
            assertEquals(NoteResult.TitleUpdated(title), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteNote() = runTest {
        noteViewModel.resultStateFlow.test {
            assertEquals(NoteResult.Loading, awaitItem())

            noteViewModel.setIdForTest(id)
            noteViewModel.deleteNote()
            assertEquals(NoteResult.Deleted, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun checkSaveChange() = runTest {
        Mockito.`when`(noteUseCase.isChanged(id, title, text)).thenReturn(true)
        Mockito.`when`(noteUseCase.isEmpty(id)).thenReturn(false)
        noteViewModel.resultStateFlow.test {
            assertEquals(NoteResult.Loading, awaitItem())

            noteViewModel.setIdForTest(id)
            noteViewModel.checkSaveChange(title, text)
            assertEquals(NoteResult.CheckSaveChange, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun checkSaveChangeNavBack() = runTest {
        Mockito.`when`(noteUseCase.isChanged(id, title, text)).thenReturn(false)
        Mockito.`when`(noteUseCase.isEmpty(id)).thenReturn(false)
        noteViewModel.resultStateFlow.test {
            assertEquals(NoteResult.Loading, awaitItem())

            noteViewModel.setIdForTest(id)
            noteViewModel.checkSaveChange(title, text)
            assertEquals(NoteResult.NavBack, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun checkSaveChangeDeleted() = runTest {
        Mockito.`when`(noteUseCase.isChanged(id, title, text)).thenReturn(false)
        Mockito.`when`(noteUseCase.isEmpty(id)).thenReturn(true)
        noteViewModel.resultStateFlow.test {
            assertEquals(NoteResult.Loading, awaitItem())

            noteViewModel.setIdForTest(id)
            noteViewModel.checkSaveChange(title, text)
            assertEquals(NoteResult.Deleted, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun saveNoteAndNavBack() = runTest {
        noteViewModel.resultStateFlow.test {
            assertEquals(NoteResult.Loading, awaitItem())

            noteViewModel.setIdForTest(id)
            noteViewModel.saveNoteAndNavBack(title, text)
            assertEquals(NoteResult.NavBack, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun doNotSaveAndNavBack() = runTest {
        Mockito.`when`(noteUseCase.isEmpty(id)).thenReturn(false)
        noteViewModel.resultStateFlow.test {
            assertEquals(NoteResult.Loading, awaitItem())

            noteViewModel.setIdForTest(id)
            noteViewModel.doNotSaveAndNavBack()
            assertEquals(NoteResult.NavBack, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun doNotSaveAndNavBackDeleted() = runTest {
        Mockito.`when`(noteUseCase.isEmpty(id)).thenReturn(true)
        noteViewModel.resultStateFlow.test {
            assertEquals(NoteResult.Loading, awaitItem())

            noteViewModel.setIdForTest(id)
            noteViewModel.doNotSaveAndNavBack()
            assertEquals(NoteResult.Deleted, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun errorResult() {
        assertEquals(NoteResult.Error(null), noteViewModel.errorResult(Throwable()))
    }
}