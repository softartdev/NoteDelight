package com.softartdev.notedelight.shared.presentation.main

import android.database.sqlite.SQLiteException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.shared.data.NoteUseCase
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.presentation.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val noteUseCase = Mockito.mock(NoteUseCase::class.java)
    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(noteUseCase)
    }

    @Test
    fun success() = runTest {
        mainViewModel.resultStateFlow.test {
            assertEquals(NoteListResult.Loading, awaitItem())

            val notes = emptyList<Note>()
            Mockito.`when`(noteUseCase.getNotes()).thenReturn(flowOf(notes))
            mainViewModel.updateNotes()
            assertEquals(NoteListResult.Success(notes), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun navMain() = runTest {
        mainViewModel.resultStateFlow.test {
            assertEquals(NoteListResult.Loading, awaitItem())

            Mockito.`when`(noteUseCase.getNotes()).thenReturn(flow { throw SQLiteException() })
            mainViewModel.updateNotes()
            assertEquals(NoteListResult.NavSignIn, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun error() = runTest {
        mainViewModel.resultStateFlow.test {
            assertEquals(NoteListResult.Loading, awaitItem())

            Mockito.`when`(noteUseCase.getNotes()).thenReturn(flow { throw Throwable() })
            mainViewModel.updateNotes()
            assertEquals(NoteListResult.Error(null), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}