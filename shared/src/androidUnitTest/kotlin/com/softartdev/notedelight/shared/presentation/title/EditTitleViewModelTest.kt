package com.softartdev.notedelight.shared.presentation.title

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.shared.date.createLocalDateTime
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.db.NoteDAO
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
    private val editTitleViewModel = EditTitleViewModel(noteDAO, updateTitleUseCase)

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
        editTitleViewModel.resetLoadingResult()
    }

    @Test
    fun loadTitle() = runTest {
        editTitleViewModel.resultStateFlow.test {
            assertEquals(EditTitleResult.Loading, awaitItem())

            editTitleViewModel.loadTitle(id)
            assertEquals(EditTitleResult.Loaded(title), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun editTitleSuccess() = runTest {
        editTitleViewModel.resultStateFlow.test {
            assertEquals(EditTitleResult.Loading, awaitItem())

            val exp = "new title"
            editTitleViewModel.editTitle(id, exp)
            val act = UpdateTitleUseCase.titleChannel.receive()
            assertEquals(exp, act)

            assertEquals(EditTitleResult.Success, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun editTitleEmptyTitleError() = runTest {
        editTitleViewModel.resultStateFlow.test {
            assertEquals(EditTitleResult.Loading, awaitItem())

            editTitleViewModel.editTitle(id, "")
            assertEquals(EditTitleResult.EmptyTitleError, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun errorResult() {
        assertEquals(EditTitleResult.Error("$id"), editTitleViewModel.errorResult(Throwable("$id")))
    }
}