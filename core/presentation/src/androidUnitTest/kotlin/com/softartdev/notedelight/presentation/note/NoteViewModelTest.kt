@file:OptIn(ExperimentalCoroutinesApi::class)

package com.softartdev.notedelight.presentation.note

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.CoroutineDispatchersStub
import com.softartdev.notedelight.PrintAntilog
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.interactor.AdaptiveInteractor
import com.softartdev.notedelight.interactor.SnackbarInteractor
import com.softartdev.notedelight.interactor.SnackbarMessage
import com.softartdev.notedelight.interactor.SnackbarTextResource
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
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import kotlin.test.assertFalse
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
    private val mockSnackbarInteractor = Mockito.mock(SnackbarInteractor::class.java)
    private val mockRouter = Mockito.mock(Router::class.java)
    private val adaptiveInteractor = AdaptiveInteractor()
    private val coroutineDispatchers = CoroutineDispatchersStub(testDispatcher = mainDispatcherRule.testDispatcher)

    private val id = 1L
    private val title: String = "title"
    private val text: String = "text"
    private val ldt: LocalDateTime = createLocalDateTime()
    private val note = Note(id, title, text, ldt, ldt)

    private val viewModel = NoteViewModel(
        adaptiveInteractor = adaptiveInteractor,
        noteDAO = mockNoteDAO,
        createNoteUseCase = mockCreateNoteUseCase,
        saveNoteUseCase = saveNoteUseCase,
        deleteNoteUseCase = mockDeleteNoteUseCase,
        snackbarInteractor = mockSnackbarInteractor,
        router = mockRouter,
        coroutineDispatchers = coroutineDispatchers
    )

    @Before
    fun setUp() = runTest {
        Napier.base(PrintAntilog())
        Mockito.`when`(mockCreateNoteUseCase.invoke()).thenReturn(id)
        Mockito.`when`(mockNoteDAO.load(id)).thenReturn(note)
    }

    @After
    fun tearDown() = runTest {
        viewModel.resetResultState(noteId = id)
        Napier.takeLogarithm()
        Mockito.reset(mockNoteDAO, mockCreateNoteUseCase, mockDeleteNoteUseCase, mockSnackbarInteractor, mockRouter)
    }

    @Test
    fun `init with noteId 0 creates new note`() = runTest {
        viewModel.resetResultState()
        viewModel.launchCollectingSelectedNoteId()
        adaptiveInteractor.selectedNoteIdStateFlow.value = 0L
        viewModel.stateFlow.test {
            val actualResult: NoteResult = awaitItem()

            assertFalse(actualResult.loading)
            assertEquals(note, actualResult.note)
            // Note: createNoteUseCase is called multiple times due to internal logic
            verify(mockCreateNoteUseCase, atLeastOnce()).invoke()
            // Note: load is called multiple times due to internal checks
            verify(mockNoteDAO, atLeastOnce()).load(id)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init with existing noteId loads note`() = runTest {
        viewModel.launchCollectingSelectedNoteId()
        adaptiveInteractor.selectedNoteIdStateFlow.value = id
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
        viewModel.launchCollectingSelectedNoteId()
        adaptiveInteractor.selectedNoteIdStateFlow.value = id
        viewModel.stateFlow.test {
            var actualResult: NoteResult = awaitItem()
            assertFalse(actualResult.loading)
            assertEquals(note, actualResult.note)

            viewModel.onAction(NoteAction.Save("", ""))
            verify(mockSnackbarInteractor).showMessage(SnackbarMessage.Resource(SnackbarTextResource.EMPTY))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun saveNote() = runTest {
        viewModel.launchCollectingSelectedNoteId()
        adaptiveInteractor.selectedNoteIdStateFlow.value = id
        viewModel.stateFlow.test {
            var actualResult: NoteResult = awaitItem()
            assertFalse(actualResult.loading)
            assertEquals(note, actualResult.note)

            viewModel.onAction(NoteAction.Save(title, text))
            verify(mockNoteDAO, times(2)).load(id)

            actualResult = awaitItem()
            assertEquals(note, actualResult.note)
            assertTrue(actualResult.loading)

            advanceUntilIdle()
            actualResult = awaitItem()
            assertFalse(actualResult.loading)
            verify(mockSnackbarInteractor).showMessage(SnackbarMessage.Resource(SnackbarTextResource.SAVED, title))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun editTitle() = runTest {
        viewModel.launchCollectingSelectedNoteId()
        adaptiveInteractor.selectedNoteIdStateFlow.value = id
        viewModel.stateFlow.test {
            var actualResult: NoteResult = awaitItem()
            assertFalse(actualResult.loading)
            assertEquals(note, actualResult.note)

            viewModel.onAction(NoteAction.Edit)
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
        viewModel.launchCollectingSelectedNoteId()
        adaptiveInteractor.selectedNoteIdStateFlow.value = id
        viewModel.stateFlow.test {
            var actualResult: NoteResult = awaitItem()
            assertFalse(actualResult.loading)
            assertEquals(note, actualResult.note)

            viewModel.onAction(NoteAction.Delete)
            verify(mockRouter).navigate(route = AppNavGraph.DeleteNoteDialog)

            DeleteNoteUseCase.deleteChannel.send(true)
            verify(mockDeleteNoteUseCase).invoke(id)

            actualResult = awaitItem()
            assertTrue(actualResult.loading)

            verify(mockSnackbarInteractor).showMessage(SnackbarMessage.Resource(SnackbarTextResource.DELETED))

            actualResult = awaitItem()
            verify(mockRouter).adaptiveNavigateBack()
            assertFalse(actualResult.loading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun checkSaveChange() = runTest {
        viewModel.launchCollectingSelectedNoteId()
        adaptiveInteractor.selectedNoteIdStateFlow.value = id
        viewModel.stateFlow.test {
            Mockito.`when`(mockNoteDAO.load(id)).thenReturn(note.copy(text = "new text"))

            viewModel.onAction(NoteAction.CheckSaveChange(title, text))
            verify(mockRouter).navigate(route = AppNavGraph.SaveChangesDialog)

            SaveNoteUseCase.dialogChannel.send(true)
            verify(mockRouter).adaptiveNavigateBack()

            verifyNoMoreInteractions(mockRouter)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun checkSaveChangeNavBack() = runTest {
        viewModel.launchCollectingSelectedNoteId()
        adaptiveInteractor.selectedNoteIdStateFlow.value = id
        viewModel.stateFlow.test {
            viewModel.onAction(NoteAction.CheckSaveChange(title, text))
            verify(mockRouter).adaptiveNavigateBack()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun checkSaveChangeDeleted() = runTest {
        viewModel.launchCollectingSelectedNoteId()
        adaptiveInteractor.selectedNoteIdStateFlow.value = id
        viewModel.stateFlow.test {
            val actualResult: NoteResult = awaitItem()
            assertFalse(actualResult.loading)
            assertEquals(note, actualResult.note)

            Mockito.`when`(mockNoteDAO.load(id)).thenReturn(note.copy(text = "", title = ""))
            viewModel.onAction(NoteAction.CheckSaveChange("", ""))
            verify(mockDeleteNoteUseCase).invoke(id)
            verify(mockRouter).adaptiveNavigateBack()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun saveNoteAndNavBack() = runTest {
        viewModel.launchCollectingSelectedNoteId()
        adaptiveInteractor.selectedNoteIdStateFlow.value = id
        viewModel.stateFlow.test {
            var actualResult: NoteResult = awaitItem()
            assertFalse(actualResult.loading)
            assertEquals(note, actualResult.note)

            viewModel.onAction(NoteAction.Save(title, text))
            actualResult = awaitItem()
            assertTrue(actualResult.loading)

            actualResult = awaitItem()
            assertFalse(actualResult.loading)

            viewModel.onAction(NoteAction.CheckSaveChange(title, text))
            verify(mockRouter).adaptiveNavigateBack()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun doNotSaveAndNavBack() = runTest {
        viewModel.launchCollectingSelectedNoteId()
        adaptiveInteractor.selectedNoteIdStateFlow.value = id
        viewModel.stateFlow.test {
            viewModel.onAction(NoteAction.CheckSaveChange(title, text))
            verify(mockRouter).adaptiveNavigateBack()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun doNotSaveAndNavBackDeleted() = runTest {
        viewModel.launchCollectingSelectedNoteId()
        adaptiveInteractor.selectedNoteIdStateFlow.value = id
        viewModel.stateFlow.test {
            val actualResult: NoteResult = awaitItem()
            assertFalse(actualResult.loading)
            assertEquals(note, actualResult.note)

            Mockito.`when`(mockNoteDAO.load(id)).thenReturn(note.copy(text = "", title = ""))
            viewModel.onAction(NoteAction.CheckSaveChange("", ""))
            verify(mockDeleteNoteUseCase).invoke(id)
            verify(mockRouter).adaptiveNavigateBack()

            cancelAndIgnoreRemainingEvents()
        }
    }
}
