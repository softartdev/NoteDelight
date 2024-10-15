@file:OptIn(ExperimentalCoroutinesApi::class)

package com.softartdev.notedelight.shared.presentation.note

import com.softartdev.notedelight.shared.CoroutineDispatchersStub
import com.softartdev.notedelight.shared.db.NoteDAO
import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.presentation.MainDispatcherRule
import com.softartdev.notedelight.shared.usecase.note.SaveNoteUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SaveViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockRouter = Mockito.mock(Router::class.java)
    private val mockNoteDAO = Mockito.mock(NoteDAO::class.java)
    private var saveNoteUseCase: SaveNoteUseCase? = null
    private val coroutineDispatchers = CoroutineDispatchersStub(
        scheduler = mainDispatcherRule.testDispatcher.scheduler
    )
    private var saveViewModel: SaveViewModel? = null

    @Before
    fun setUp() = runTest(context = coroutineDispatchers.default) {
        saveNoteUseCase = SaveNoteUseCase(mockNoteDAO)
        saveViewModel = SaveViewModel(saveNoteUseCase!!, mockRouter, coroutineDispatchers)
    }

    @After
    fun tearDown() = runTest(context = coroutineDispatchers.default) {
        saveNoteUseCase = null
        saveViewModel = null
    }

    @Test
    fun `save and nav back`() = runTest {
        var dialogResult: Boolean? = null
        val job = launch { dialogResult = saveNoteUseCase!!.receiveDialogResult() }

        saveViewModel!!.saveNoteAndNavBack()
        advanceUntilIdle()

        job.join()
        assertTrue(dialogResult!!)
        Mockito.verify(mockRouter).popBackStack()
        Mockito.verifyNoMoreInteractions(mockRouter)
    }

    @Test
    fun `don't save and nav back`() = runTest {
        var dialogResult: Boolean? = null
        val job = launch { dialogResult = saveNoteUseCase!!.receiveDialogResult() }

        saveViewModel!!.doNotSaveAndNavBack()
        advanceUntilIdle()

        job.join()
        assertFalse(dialogResult!!)
        Mockito.verify(mockRouter).popBackStack()
        Mockito.verifyNoMoreInteractions(mockRouter)
    }

    @Test
    fun `navigate up`() = runTest {
        saveViewModel!!.navigateUp()
        advanceUntilIdle()
        Mockito.verify(mockRouter).popBackStack()
        Mockito.verifyNoMoreInteractions(mockRouter)
    }
}