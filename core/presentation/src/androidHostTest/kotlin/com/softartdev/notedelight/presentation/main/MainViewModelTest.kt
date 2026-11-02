package com.softartdev.notedelight.presentation.main

import android.database.sqlite.SQLiteException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import app.cash.turbine.test
import co.touchlab.kermit.Logger
import com.russhwolf.settings.MapSettings
import com.softartdev.notedelight.CoroutineDispatchersStub
import com.softartdev.notedelight.PrintLogWriter
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.interactor.AdaptiveInteractor
import com.softartdev.notedelight.interactor.ShareInteractorStub
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.model.NoteSort
import com.softartdev.notedelight.model.SortField
import com.softartdev.notedelight.model.SortOrder
import com.softartdev.notedelight.navigation.AppNavGraph
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.presentation.MainDispatcherRule
import com.softartdev.notedelight.repository.PreferencesRepo
import com.softartdev.notedelight.repository.PreferencesRepoImpl
import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.usecase.note.CreateNoteUseCase
import com.softartdev.notedelight.usecase.note.NoteSortUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val mockCreateNoteUseCase = Mockito.mock(CreateNoteUseCase::class.java)
    private val adaptiveInteractor = AdaptiveInteractor()
    private val shareInteractor = ShareInteractorStub()
    private val coroutineDispatchers = CoroutineDispatchersStub(testDispatcher = mainDispatcherRule.testDispatcher)
    private lateinit var preferencesRepo: PreferencesRepo
    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() = runTest {
        Logger.setLogWriters(PrintLogWriter())
        shareInteractor.reset()
        NoteDAO.noteSortFlow.value = NoteSort()
        preferencesRepo = PreferencesRepoImpl(MapSettings())
        Mockito.`when`(mockSafeRepo.noteDAO).thenReturn(mockNoteDAO)
        Mockito.`when`(mockNoteDAO.count()).thenReturn(0)
        mainViewModel = MainViewModel(
            safeRepo = mockSafeRepo,
            router = mockRouter,
            adaptiveInteractor = adaptiveInteractor,
            shareInteractor = shareInteractor,
            createNoteUseCase = mockCreateNoteUseCase,
            noteSortUseCase = NoteSortUseCase(preferencesRepo),
            coroutineDispatchers = coroutineDispatchers,
        )
    }

    @After
    fun tearDown() = runTest {
        NoteDAO.noteSortFlow.value = NoteSort()
        shareInteractor.reset()
        Mockito.reset(mockSafeRepo, mockRouter, mockNoteDAO, mockCreateNoteUseCase)
        Logger.setLogWriters()
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
    fun onNoteClicked() = runTest {
        mainViewModel.onAction(MainAction.OnNoteClick(1))
        Mockito.verify(mockRouter).adaptiveNavigateToDetail(contentKey = 1)
    }

    @Test
    fun onSettingsClicked() {
        mainViewModel.onAction(MainAction.OnSettingsClick)
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

    @Test
    fun sharedTextCreatesAndOpensNote() = runTest {
        val sharedText = "Shared note content"
        val noteId = 1L
        val pagingFlow = flowOf(PagingData.empty<Note>())
        Mockito.`when`(mockNoteDAO.pagingDataFlow).thenReturn(pagingFlow)
        Mockito.`when`(mockCreateNoteUseCase.invoke(text = sharedText)).thenReturn(noteId)
        shareInteractor.sharedTextStateFlow.value = sharedText

        mainViewModel.stateFlow.test {
            assertEquals(NoteListResult.Loading, awaitItem())

            mainViewModel.launchNotes()
            var result: NoteListResult = awaitItem()
            if (result is NoteListResult.Success && result.selectedId == null) {
                result = awaitItem()
            }
            assertTrue(result is NoteListResult.Success)
            assertEquals(noteId, (result as NoteListResult.Success).selectedId)
            assertEquals(null, shareInteractor.sharedTextStateFlow.value)
            Mockito.verify(mockCreateNoteUseCase).invoke(text = sharedText)
            Mockito.verify(mockRouter).adaptiveNavigateToDetail(contentKey = noteId)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onSortChangeUpdatesPreferencesDaoFlowAndSuccessState() = runTest {
        val pagingFlow = flowOf(PagingData.empty<Note>())
        Mockito.`when`(mockNoteDAO.pagingDataFlow).thenReturn(pagingFlow)

        mainViewModel.stateFlow.test {
            assertEquals(NoteListResult.Loading, awaitItem())

            mainViewModel.launchNotes()
            val initial = awaitItem() as NoteListResult.Success
            assertEquals(NoteSort(SortField.DATE_MODIFIED, SortOrder.DESC), initial.noteSort)

            val newSort = NoteSort(SortField.TITLE, SortOrder.ASC)
            mainViewModel.onAction(MainAction.OnSortChange(newSort))

            val updated = awaitItem() as NoteListResult.Success
            assertEquals(newSort, updated.noteSort)
            assertEquals(newSort.field, preferencesRepo.sortField)
            assertEquals(newSort.order, preferencesRepo.sortOrder)
            assertEquals(newSort, NoteDAO.noteSortFlow.value)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
