package com.softartdev.notedelight.presentation.title

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.PrintAntilog
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.presentation.MainDispatcherRule
import com.softartdev.notedelight.usecase.note.UpdateTitleUseCase
import com.softartdev.notedelight.util.createLocalDateTime
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class EditTitleViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val id = 1L
    private val title = "test title"
    private val note = Note(
        id = id,
        title = title,
        text = "text",
        dateCreated = createLocalDateTime(),
        dateModified = createLocalDateTime()
    )

    private val mockNoteDAO = Mockito.mock(NoteDAO::class.java)
    private val updateTitleUseCase = UpdateTitleUseCase(mockNoteDAO)
    private val mockRouter = Mockito.mock(Router::class.java)
    private val viewModel = EditTitleViewModel(
        noteId = id,
        noteDAO = mockNoteDAO,
        updateTitleUseCase = updateTitleUseCase,
        router = mockRouter
    )

    @Before
    fun setUp() = runTest {
        Napier.base(PrintAntilog())
        Mockito.`when`(mockNoteDAO.load(id)).thenReturn(note)
    }

    @After
    fun tearDown() = runTest {
        Napier.takeLogarithm()
        Mockito.reset(mockNoteDAO, mockRouter)
    }

    @Test
    fun `init loads title`() = runTest {
        viewModel.stateFlow.test {
            val initialState = awaitItem()
            assertFalse(initialState.loading)

            viewModel.loadTitle()

            val loadedState: EditTitleResult = awaitItem()
            assertEquals(title, loadedState.title)
            assertFalse(loadedState.loading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load title success`() = runTest {
        viewModel.stateFlow.test {
            val initialState = awaitItem()
            assertFalse(initialState.loading)
            assertTrue(initialState.title.isEmpty())

            viewModel.loadTitle()

            val loadedState = awaitItem()
            assertEquals(title, loadedState.title)
            assertFalse(loadedState.loading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load title error`() = runTest {
        val error = RuntimeException("Load error")
        Mockito.`when`(mockNoteDAO.load(id)).thenThrow(error)

        viewModel.stateFlow.test {
            val initialState = awaitItem()
            assertFalse(initialState.loading)

            viewModel.loadTitle()

            val errorState = awaitItem()
            assertFalse(errorState.loading)
            assertEquals(error.message, errorState.snackBarMessageType)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
