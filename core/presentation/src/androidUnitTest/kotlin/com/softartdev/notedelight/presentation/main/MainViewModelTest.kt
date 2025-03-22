package com.softartdev.notedelight.presentation.main

import android.database.sqlite.SQLiteException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import app.cash.turbine.test
import com.softartdev.notedelight.CoroutineDispatchersStub
import com.softartdev.notedelight.PrintAntilog
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.navigation.AppNavGraph
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.presentation.MainDispatcherRule
import com.softartdev.notedelight.repository.SafeRepo
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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

    private val mockSafeRepo = Mockito.mock(SafeRepo::class.java)
    private val mockRouter = Mockito.mock(Router::class.java)
    private val mockNoteDAO = Mockito.mock(NoteDAO::class.java)
    private val coroutineDispatchers = CoroutineDispatchersStub(testDispatcher = mainDispatcherRule.testDispatcher)
    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() {
        Napier.base(PrintAntilog())
        Mockito.`when`(mockSafeRepo.noteDAO).thenReturn(mockNoteDAO)
        mainViewModel = MainViewModel(mockSafeRepo, mockRouter, coroutineDispatchers)
    }

    @After
    fun tearDown() = runTest {
        Mockito.reset(mockSafeRepo, mockRouter, mockNoteDAO)
        Napier.takeLogarithm()
    }

    @Test
    fun success() = runTest {
        mainViewModel.stateFlow.test {
            assertEquals(NoteListResult.Loading, awaitItem())

            val pagingFlow = flowOf(PagingData.empty<Note>())
            Mockito.`when`(mockNoteDAO.pagingDataFlow).thenReturn(pagingFlow)

            mainViewModel.launchNotes()
            assertTrue(awaitItem() is NoteListResult.Success)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun navMain() = runTest {
        mainViewModel.stateFlow.test {
            assertEquals(NoteListResult.Loading, awaitItem())

            Mockito.`when`(mockNoteDAO.pagingDataFlow).thenThrow(SQLiteException())

            mainViewModel.launchNotes()
            assertEquals(NoteListResult.Error(null), awaitItem())
            Mockito.verify(mockRouter).navigateClearingBackStack(route = AppNavGraph.Splash)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onNoteClicked() {
        mainViewModel.onNoteClicked(1)
        Mockito.verify(mockRouter).navigate(route = AppNavGraph.Details(noteId = 1))
    }

    @Test
    fun onSettingsClicked() {
        mainViewModel.onSettingsClicked()
        Mockito.verify(mockRouter).navigate(route = AppNavGraph.Settings)
    }

    @Test
    fun error() = runTest {
        mainViewModel.stateFlow.test {
            assertEquals(NoteListResult.Loading, awaitItem())

            Mockito.`when`(mockNoteDAO.pagingDataFlow).thenThrow(RuntimeException())

            mainViewModel.launchNotes()
            assertEquals(NoteListResult.Error(null), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}
