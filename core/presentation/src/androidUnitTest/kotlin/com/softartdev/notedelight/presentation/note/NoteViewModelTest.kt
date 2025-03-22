@file:OptIn(ExperimentalCoroutinesApi::class)

package com.softartdev.notedelight.presentation.note

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.CoroutineDispatchersStub
import com.softartdev.notedelight.PrintAntilog
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.navigation.AppNavGraph
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.presentation.MainDispatcherRule
import com.softartdev.notedelight.usecase.note.CreateNoteUseCase
import com.softartdev.notedelight.usecase.note.DeleteNoteUseCase
import com.softartdev.notedelight.usecase.note.SaveNoteUseCase
import com.softartdev.notedelight.usecase.note.UpdateTitleUseCase
import com.softartdev.notedelight.util.createLocalDateTime
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

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

    private val id = 1L
    private val title: String = "title"
    private val text: String = "text"
    private val ldt: LocalDateTime = createLocalDateTime()
    private val note = Note(id, title, text, ldt, ldt)

    private val viewModel = NoteViewModel(
        noteId = id,
        noteDAO = mockNoteDAO,
        createNoteUseCase = mockCreateNoteUseCase,
        saveNoteUseCase = saveNoteUseCase,
        deleteNoteUseCase = mockDeleteNoteUseCase,
        router = mockRouter,
        coroutineDispatchers = coroutineDispatchers
    )

    @Before
    fun setUp() = runTest {
        Napier.base(PrintAntilog())
        Mockito.`when`(mockCreateNoteUseCase.invoke()).thenReturn(id)
        Mockito.`when`(mockNoteDAO.load(id)).thenReturn(note)
        viewModel.createOrLoadNote()
    }

    @After
    fun tearDown() = runTest {
        viewModel.resetResultState(noteId = id)
        Napier.takeLogarithm()
        Mockito.reset(mockNoteDAO, mockCreateNoteUseCase, mockDeleteNoteUseCase, mockRouter)
    }

    @Test
    fun `init with noteId 0 creates new note`() = runTest {
        viewModel.resetResultState()
        viewModel.createOrLoadNote()
        viewModel.stateFlow.test {
            val actualResult: NoteResult = awaitItem()

            assertFalse(actualResult.loading)
            assertEquals(note, actualResult.note)
            verify(mockCreateNoteUseCase).invoke()
            verify(mockNoteDAO, times(2)).load(id) // 1st time in @Before

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init with existing noteId loads note`() = runTest {
        viewModel.stateFlow.test {
            val actualResult: NoteResult = awaitItem()

            assertFalse(actualResult.loading)
            assertEquals(note, actualResult.note)
            verify(mockNoteDAO).load(id)
            verifyNoMoreInteractions(mockCreateNoteUseCase)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun saveNoteEmpty() = runTest {
        viewModel.stateFlow.test {
            var actualResult: NoteResult = awaitItem()
            assertFalse(actualResult.loading)
            assertEquals(note, actualResult.note)

            viewModel.stateFlow.value.onSaveClick("", "")
            actualResult = awaitItem()
            assertEquals(NoteResult.SnackBarMessageType.EMPTY, actualResult.snackBarMessageType)

            viewModel.stateFlow.value.disposeOneTimeEvents()
            actualResult = awaitItem()
            assertNull(actualResult.snackBarMessageType)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun saveNote() = runTest {
        viewModel.stateFlow.test {
            var actualResult: NoteResult = awaitItem()
            assertFalse(actualResult.loading)
            assertEquals(note, actualResult.note)

            viewModel.stateFlow.value.onSaveClick(title, text)
            verify(mockNoteDAO, times(2)).load(id)

            actualResult = awaitItem()
            assertEquals(note, actualResult.note)
            assertTrue(actualResult.loading)

            advanceUntilIdle()
            actualResult = awaitItem()
            assertFalse(actualResult.loading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun editTitle() = runTest {
        viewModel.stateFlow.test {
            var actualResult: NoteResult = awaitItem()
            assertFalse(actualResult.loading)
            assertEquals(note, actualResult.note)

            viewModel.stateFlow.value.onEditClick()
            verify(mockRouter).navigate(route = AppNavGraph.EditTitleDialog(noteId = id))

            UpdateTitleUseCase.dialogChannel.send(title)
            actualResult = awaitItem()
            assertTrue(actualResult.loading)
            assertEquals(title, actualResult.note?.title)

            advanceUntilIdle()
            actualResult = awaitItem()
            assertFalse(actualResult.loading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteNote() = runTest {
        viewModel.stateFlow.test {
            var actualResult: NoteResult = awaitItem()
            assertFalse(actualResult.loading)
            assertEquals(note, actualResult.note)

            viewModel.stateFlow.value.onDeleteClick()
            verify(mockRouter).navigate(route = AppNavGraph.DeleteNoteDialog)

            DeleteNoteUseCase.deleteChannel.send(true)
            verify(mockDeleteNoteUseCase).invoke(id)

            actualResult = awaitItem()
            assertTrue(actualResult.loading)
            assertNull(actualResult.snackBarMessageType)

            actualResult = awaitItem()
            verify(mockRouter).popBackStack(route = AppNavGraph.Main, inclusive = false, saveState = false)
            assertFalse(actualResult.loading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun checkSaveChange() = runTest {
        viewModel.stateFlow.test {
            Mockito.`when`(mockNoteDAO.load(id)).thenReturn(note.copy(text = "new text"))

            viewModel.stateFlow.value.checkSaveChange(title, text)
            verify(mockRouter).navigate(route = AppNavGraph.SaveChangesDialog)

            SaveNoteUseCase.dialogChannel.send(true)
            verify(mockRouter).navigateClearingBackStack(route = AppNavGraph.Main)

            verifyNoMoreInteractions(mockRouter)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun checkSaveChangeNavBack() = runTest {
        viewModel.stateFlow.test {
            viewModel.stateFlow.value.checkSaveChange(title, text)
            verify(mockRouter).popBackStack()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun checkSaveChangeDeleted() = runTest {
        viewModel.stateFlow.test {
            val actualResult: NoteResult = awaitItem()
            assertFalse(actualResult.loading)
            assertEquals(note, actualResult.note)

            Mockito.`when`(mockNoteDAO.load(id)).thenReturn(note.copy(text = "", title = ""))
            viewModel.stateFlow.value.checkSaveChange("", "")
            verify(mockDeleteNoteUseCase).invoke(id)
            verify(mockRouter).popBackStack(route = AppNavGraph.Main, inclusive = false, saveState = false)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun saveNoteAndNavBack() = runTest {
        viewModel.stateFlow.test {
            var actualResult: NoteResult = awaitItem()
            assertFalse(actualResult.loading)
            assertEquals(note, actualResult.note)

            viewModel.stateFlow.value.onSaveClick(title, text)
            actualResult = awaitItem()
            assertTrue(actualResult.loading)

            actualResult = awaitItem()
            assertFalse(actualResult.loading)

            viewModel.stateFlow.value.checkSaveChange(title, text)
            verify(mockRouter).popBackStack()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun doNotSaveAndNavBack() = runTest {
        viewModel.stateFlow.test {
            viewModel.stateFlow.value.checkSaveChange(title, text)
            verify(mockRouter).popBackStack()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun doNotSaveAndNavBackDeleted() = runTest {
        viewModel.stateFlow.test {
            val actualResult: NoteResult = awaitItem()
            assertFalse(actualResult.loading)
            assertEquals(note, actualResult.note)

            Mockito.`when`(mockNoteDAO.load(id)).thenReturn(note.copy(text = "", title = ""))
            viewModel.stateFlow.value.checkSaveChange("", "")
            verify(mockDeleteNoteUseCase).invoke(id)
            verify(mockRouter).popBackStack(route = AppNavGraph.Main, inclusive = false, saveState = false)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
