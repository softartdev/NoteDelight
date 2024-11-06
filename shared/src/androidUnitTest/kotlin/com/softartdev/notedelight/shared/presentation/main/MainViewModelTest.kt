package com.softartdev.notedelight.shared.presentation.main

import android.database.sqlite.SQLiteException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.paging.PagingSource
import app.cash.paging.PagingState
import app.cash.turbine.test
import com.softartdev.notedelight.shared.CoroutineDispatchersStub
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.db.NoteDAO
import com.softartdev.notedelight.shared.db.SafeRepo
import com.softartdev.notedelight.shared.navigation.AppNavGraph
import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.presentation.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
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
        Mockito.`when`(mockSafeRepo.noteDAO).thenReturn(mockNoteDAO)
        mainViewModel = MainViewModel(mockSafeRepo, mockRouter, coroutineDispatchers)
    }

    @Test
    fun success() = runTest {
        mainViewModel.stateFlow.test {
            assertEquals(NoteListResult.Loading, awaitItem())

            val pagingSource = object : PagingSource<Int, Note>() {
                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Note> {
                    return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
                }
                override fun getRefreshKey(state: PagingState<Int, Note>): Int? = null
            }
            Mockito.`when`(mockNoteDAO.pagingSource).thenReturn(pagingSource)

            mainViewModel.updateNotes()

            assertTrue(awaitItem() is NoteListResult.Success)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun navMain() = runTest {
        mainViewModel.stateFlow.test {
            assertEquals(NoteListResult.Loading, awaitItem())

            Mockito.`when`(mockNoteDAO.pagingSource).thenThrow(SQLiteException())
            mainViewModel.updateNotes()
            assertEquals(NoteListResult.Error(null), awaitItem())
            Mockito.verify(mockRouter).navigateClearingBackStack(route = AppNavGraph.SignIn)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onNoteClicked() {
        mainViewModel.onNoteClicked(1)
        Mockito.verify(mockRouter).navigate(route = AppNavGraph.Details(noteId = 1))
    }

    @Test
    fun error() = runTest {
        mainViewModel.stateFlow.test {
            assertEquals(NoteListResult.Loading, awaitItem())

            Mockito.`when`(mockNoteDAO.pagingSource).thenThrow(RuntimeException())
            mainViewModel.updateNotes()
            assertEquals(NoteListResult.Error(null), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}
