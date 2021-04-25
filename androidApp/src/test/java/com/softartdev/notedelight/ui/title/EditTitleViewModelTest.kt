package com.softartdev.notedelight.ui.title

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.shared.data.NoteUseCase
import com.softartdev.notedelight.shared.date.createLocalDateTime
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.test.util.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.datetime.LocalDateTime
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class EditTitleViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val noteUseCase = Mockito.mock(NoteUseCase::class.java)
    private val editTitleViewModel = EditTitleViewModel(noteUseCase)

    private val id = 1L
    private val title: String = "title"
    private val text: String = "text"
    private val ldt: LocalDateTime = createLocalDateTime()
    private val note = Note(id, title, text, ldt, ldt)
    private val titleChannel = Channel<String>()

    @Before
    fun setUp() = mainCoroutineRule.runBlockingTest {
        Mockito.`when`(noteUseCase.loadNote(id)).thenReturn(note)
        Mockito.`when`(noteUseCase.updateTitle(id, title)).thenReturn(1)
        Mockito.`when`(noteUseCase.titleChannel).thenReturn(titleChannel)
    }

    @After
    fun tearDown() = mainCoroutineRule.runBlockingTest {
        editTitleViewModel.resetLoadingResult()
    }

    @Test
    fun loadTitle() = runBlocking {
        editTitleViewModel.resultStateFlow.test {
            assertEquals(EditTitleResult.Loading, expectItem())

            editTitleViewModel.loadTitle(id)
            assertEquals(EditTitleResult.Loaded(title), expectItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun editTitleSuccess() = runBlocking {
        editTitleViewModel.resultStateFlow.test {
            assertEquals(EditTitleResult.Loading, expectItem())

            val exp = "new title"
            editTitleViewModel.editTitle(id, exp)
            val act = titleChannel.receive()
            assertEquals(exp, act)

            assertEquals(EditTitleResult.Success, expectItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun editTitleEmptyTitleError() = runBlocking {
        editTitleViewModel.resultStateFlow.test {
            assertEquals(EditTitleResult.Loading, expectItem())

            editTitleViewModel.editTitle(id, "")
            assertEquals(EditTitleResult.EmptyTitleError, expectItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun errorResult() {
        assertEquals(EditTitleResult.Error("$id"), editTitleViewModel.errorResult(Throwable("$id")))
    }
}