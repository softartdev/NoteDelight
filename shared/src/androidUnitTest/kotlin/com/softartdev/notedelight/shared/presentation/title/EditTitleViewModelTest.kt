package com.softartdev.notedelight.shared.presentation.title

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.shared.date.createLocalDateTime
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.db.NoteDAO
import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.presentation.MainDispatcherRule
import com.softartdev.notedelight.shared.usecase.note.UpdateTitleUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class EditTitleViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val noteDAO = Mockito.mock(NoteDAO::class.java)
    private val updateTitleUseCase = UpdateTitleUseCase(noteDAO)
    private val router = Mockito.mock(Router::class.java)
    private val editTitleViewModel = EditTitleViewModel(noteDAO, updateTitleUseCase, router)

    private val id = 1L
    private val title: String = "title"
    private val text: String = "text"
    private val ldt: LocalDateTime = createLocalDateTime()
    private val note = Note(id, title, text, ldt, ldt)

    @Before
    fun setUp() = runTest {
        Mockito.`when`(noteDAO.load(id)).thenReturn(note)
    }

    @After
    fun tearDown() = runTest {
//        editTitleViewModel.resetLoadingResult()
    }

    @Test
    fun loadTitle() = runTest {
        editTitleViewModel.stateFlow.test {
            assertEquals(EditTitleResult.Loading, awaitItem())

            editTitleViewModel.loadTitle(id)
            assertEquals(EditTitleResult.Loaded(title), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun editTitleSuccess() = runTest {
        editTitleViewModel.stateFlow.test {
            assertEquals(EditTitleResult.Loading, awaitItem())

            val exp = "new title"
            editTitleViewModel.editTitle(id, exp)
            val act = UpdateTitleUseCase.titleChannel.receive()
            assertEquals(exp, act)

            Mockito.verify(router).popBackStack()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun editTitleEmptyTitleError() = runTest {
        editTitleViewModel.stateFlow.test {
            assertEquals(EditTitleResult.Loading, awaitItem())

            editTitleViewModel.editTitle(id, "")
            assertEquals(EditTitleResult.EmptyTitleError, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}