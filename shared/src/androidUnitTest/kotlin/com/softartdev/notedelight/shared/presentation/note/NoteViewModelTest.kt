@file:OptIn(ExperimentalCoroutinesApi::class)

package com.softartdev.notedelight.shared.presentation.note

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.shared.CoroutineDispatchersStub
import com.softartdev.notedelight.shared.PrintAntilog
import com.softartdev.notedelight.shared.date.createLocalDateTime
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.db.NoteDAO
import com.softartdev.notedelight.shared.navigation.AppNavGraph
import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.presentation.MainDispatcherRule
import com.softartdev.notedelight.shared.usecase.note.CreateNoteUseCase
import com.softartdev.notedelight.shared.usecase.note.DeleteNoteUseCase
import com.softartdev.notedelight.shared.usecase.note.SaveNoteUseCase
import com.softartdev.notedelight.shared.usecase.note.UpdateTitleUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class NoteViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockNoteDAO = Mockito.mock(NoteDAO::class.java)
    private val mockCreateNoteUseCase = Mockito.mock(CreateNoteUseCase::class.java)
    private val saveNoteUseCase = SaveNoteUseCase(mockNoteDAO)
    private val mockDeleteNoteUseCase = Mockito.mock(DeleteNoteUseCase::class.java)
    private val mockRouter = Mockito.mock(Router::class.java)
    private val coroutineDispatchers = CoroutineDispatchersStub(testDispatcher = mainDispatcherRule.testDispatcher)
    private val noteViewModel = NoteViewModel(mockNoteDAO, mockCreateNoteUseCase, saveNoteUseCase, mockDeleteNoteUseCase, mockRouter, coroutineDispatchers)

    private val id = 1L
    private val title: String = "title"
    private val text: String = "text"
    private val ldt: LocalDateTime = createLocalDateTime()
    private val note = Note(id, title, text, ldt, ldt)

    @Before
    fun setUp() = runTest {
        Napier.base(PrintAntilog())
        Mockito.`when`(mockCreateNoteUseCase.invoke()).thenReturn(id)
        Mockito.`when`(mockNoteDAO.load(id)).thenReturn(note)
    }

    @After
    fun tearDown() = runTest {
        noteViewModel.onCleared()
        Napier.takeLogarithm()
    }

    @Test
    fun createNote() = runTest {
        noteViewModel.stateFlow.test {
            assertEquals(NoteResult.Loading, awaitItem())

            noteViewModel.createNote()
            assertEquals(NoteResult.Created(id), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadNote() = runTest {
        noteViewModel.stateFlow.test {
            assertEquals(NoteResult.Loading, awaitItem())

            noteViewModel.loadNote(id)
            assertEquals(NoteResult.Loaded(note), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun saveNoteEmpty() = runTest {
        noteViewModel.stateFlow.test {
            assertEquals(NoteResult.Loading, awaitItem())

            noteViewModel.saveNote("", "")
            assertEquals(NoteResult.Empty, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun saveNote() = runTest {
        noteViewModel.stateFlow.test {
            assertEquals(NoteResult.Loading, awaitItem())

            noteViewModel.setIdForTest(id)
            noteViewModel.saveNote(title, text)
            assertEquals(NoteResult.Saved(title), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun editTitle() = runTest {
        noteViewModel.stateFlow.test {
            assertEquals(NoteResult.Loading, awaitItem())

            noteViewModel.setIdForTest(id)
            noteViewModel.editTitle()
            Mockito.verify(mockRouter).navigate(route = AppNavGraph.EditTitleDialog(noteId = id))

            UpdateTitleUseCase.titleChannel.send(title)
            assertEquals(NoteResult.TitleUpdated(title), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteNote() = runTest {
        noteViewModel.stateFlow.test {
            assertEquals(NoteResult.Loading, awaitItem())

            noteViewModel.setIdForTest(id)
            noteViewModel.deleteNote()
            assertEquals(NoteResult.Deleted, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun checkSaveChange() = runTest {
        noteViewModel.stateFlow.test {
            assertEquals(NoteResult.Loading, awaitItem())

            noteViewModel.setIdForTest(id)
            Mockito.`when`(mockNoteDAO.load(id)).thenReturn(note.copy(text = "new text"))
            noteViewModel.checkSaveChange(title, text)
            Mockito.verify(mockRouter).navigate(route = AppNavGraph.SaveChangesDialog)

            SaveNoteUseCase.dialogChannel.send(true)
            Mockito.verify(mockRouter).popBackStack()

            Mockito.verifyNoMoreInteractions(mockRouter)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun checkSaveChangeNavBack() = runTest {
        noteViewModel.stateFlow.test {
            assertEquals(NoteResult.Loading, awaitItem())

            noteViewModel.setIdForTest(id)
            noteViewModel.checkSaveChange(title, text)
            Mockito.verify(mockRouter).popBackStack()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun checkSaveChangeDeleted() = runTest {
        noteViewModel.stateFlow.test {
            assertEquals(NoteResult.Loading, awaitItem())

            noteViewModel.setIdForTest(id)
            Mockito.`when`(mockNoteDAO.load(id)).thenReturn(note.copy(text = "", title = ""))
            noteViewModel.checkSaveChange("", "")
            assertEquals(NoteResult.Deleted, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun saveNoteAndNavBack() = runTest {
        noteViewModel.stateFlow.test {
            assertEquals(NoteResult.Loading, awaitItem())

            noteViewModel.setIdForTest(id)
            noteViewModel.saveNoteAndNavBack(title, text)
            Mockito.verify(mockRouter).popBackStack()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun doNotSaveAndNavBack() = runTest {
        noteViewModel.stateFlow.test {
            assertEquals(NoteResult.Loading, awaitItem())

            noteViewModel.setIdForTest(id)
            noteViewModel.doNotSaveAndNavBack()
            Mockito.verify(mockRouter).popBackStack()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun doNotSaveAndNavBackDeleted() = runTest {
        noteViewModel.stateFlow.test {
            assertEquals(NoteResult.Loading, awaitItem())

            noteViewModel.setIdForTest(id)
            Mockito.`when`(mockNoteDAO.load(id)).thenReturn(note.copy(text = "", title = ""))
            noteViewModel.doNotSaveAndNavBack()
            assertEquals(NoteResult.Deleted, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}