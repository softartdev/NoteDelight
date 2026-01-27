package com.softartdev.notedelight.presentation.adaptive

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import app.cash.turbine.test
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.CoroutineDispatchersStub
import com.softartdev.notedelight.PrintLogWriter
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.interactor.AdaptiveInteractor
import com.softartdev.notedelight.interactor.SnackbarInteractor
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.presentation.MainDispatcherRule
import com.softartdev.notedelight.presentation.main.MainAction
import com.softartdev.notedelight.presentation.main.MainViewModel
import com.softartdev.notedelight.presentation.main.NoteListResult
import com.softartdev.notedelight.presentation.note.NoteAction
import com.softartdev.notedelight.presentation.note.NoteViewModel
import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.usecase.note.CreateNoteUseCase
import com.softartdev.notedelight.usecase.note.DeleteNoteUseCase
import com.softartdev.notedelight.usecase.note.SaveNoteUseCase
import com.softartdev.notedelight.util.createLocalDateTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class AdaptiveInteractorTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockSafeRepo = Mockito.mock(SafeRepo::class.java)
    private val mockRouter = Mockito.mock(Router::class.java)
    private val mockNoteDAO = Mockito.mock(NoteDAO::class.java)
    private val mockCreateNoteUseCase = Mockito.mock(CreateNoteUseCase::class.java)
    private val mockDeleteNoteUseCase = Mockito.mock(DeleteNoteUseCase::class.java)
    private val mockSnackbarInteractor = Mockito.mock(SnackbarInteractor::class.java)
    private val adaptiveInteractor = AdaptiveInteractor()
    private val coroutineDispatchers = CoroutineDispatchersStub(mainDispatcherRule.testDispatcher)
    
    private lateinit var mainViewModel: MainViewModel
    private lateinit var noteViewModel: NoteViewModel

    private val id = 1L
    private val title: String = "title"
    private val text: String = "text"
    private val ldt: LocalDateTime = createLocalDateTime()
    private val note = Note(id, title, text, ldt, ldt)

    @Before
    fun setUp() = runTest {
        Logger.setLogWriters(PrintLogWriter())

        mainViewModel = MainViewModel(
            safeRepo = mockSafeRepo,
            router = mockRouter,
            adaptiveInteractor = adaptiveInteractor,
            coroutineDispatchers = coroutineDispatchers
        )
        noteViewModel = NoteViewModel(
            adaptiveInteractor = adaptiveInteractor,
            noteDAO = mockNoteDAO,
            createNoteUseCase = mockCreateNoteUseCase,
            saveNoteUseCase = SaveNoteUseCase(mockNoteDAO),
            deleteNoteUseCase = mockDeleteNoteUseCase,
            snackbarInteractor = mockSnackbarInteractor,
            router = mockRouter,
            coroutineDispatchers = coroutineDispatchers
        )
        Mockito.`when`(mockNoteDAO.pagingDataFlow).thenReturn(flowOf(PagingData.empty()))
        Mockito.`when`(mockSafeRepo.noteDAO).thenReturn(mockNoteDAO)
        Mockito.`when`(mockNoteDAO.count()).thenReturn(0)
        Mockito.`when`(mockCreateNoteUseCase.invoke()).thenReturn(id)
        Mockito.`when`(mockNoteDAO.load(id)).thenReturn(note)
    }

    @After
    fun tearDown() = runTest {
        Mockito.reset(mockSafeRepo, mockRouter, mockNoteDAO, mockCreateNoteUseCase, mockDeleteNoteUseCase, mockSnackbarInteractor)
        Logger.setLogWriters()
    }

    @Test
    fun `when MainViewModel creates or selects note then it reflects on NoteViewModel`() = runTest {
        mainViewModel.launchNotes()
        noteViewModel.launchCollectingSelectedNoteId()
        
        mainViewModel.stateFlow.test {
            val initialResult = awaitItem()
            assertTrue(initialResult is NoteListResult.Success)
            assertNull(initialResult.selectedId)
            
            mainViewModel.onAction(MainAction.OnNoteClick(id))
            
            val updatedResult = awaitItem()
            assertTrue(updatedResult is NoteListResult.Success)
            assertEquals(id, updatedResult.selectedId)
            
            cancelAndIgnoreRemainingEvents()
        }
        noteViewModel.stateFlow.test {
            var noteResult = awaitItem()
            while (noteResult.note == null && !noteResult.loading) {
                noteResult = awaitItem()
            }
            if (noteResult.loading) {
                noteResult = awaitItem()
            }
            assertFalse(noteResult.loading)
            assertEquals(note, noteResult.note)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when NoteViewModel navigate back then MainViewModel has nothing selected`() = runTest {
        mainViewModel.launchNotes()
        noteViewModel.launchCollectingSelectedNoteId()
        
        mainViewModel.onAction(MainAction.OnNoteClick(id))
        
        mainViewModel.stateFlow.test {
            val selectedResult = awaitItem()
            assertTrue(selectedResult is NoteListResult.Success)
            assertEquals(id, selectedResult.selectedId)
            
            noteViewModel.onAction(NoteAction.CheckSaveChange(text))
            
            val clearedResult = awaitItem()
            assertTrue(clearedResult is NoteListResult.Success)
            assertNull(clearedResult.selectedId)
            
            cancelAndIgnoreRemainingEvents()
        }
        Mockito.verify(mockRouter).adaptiveNavigateBack()
    }

    @Test
    fun `when create clicked with selected note then request save changes`() = runTest {
        adaptiveInteractor.selectedNoteIdStateFlow.value = id
        val deferred = async { adaptiveInteractor.checkSaveChangeChannel.receive() }
        
        mainViewModel.onAction(MainAction.OnNoteClick(0))
        
        deferred.await()
        assertEquals(id, adaptiveInteractor.selectedNoteIdStateFlow.value)
    }
}
