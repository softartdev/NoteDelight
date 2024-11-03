@file:OptIn(ExperimentalCoroutinesApi::class)

package com.softartdev.notedelight.shared.presentation.note

import com.softartdev.notedelight.shared.CoroutineDispatchersStub
import com.softartdev.notedelight.shared.PrintAntilog
import com.softartdev.notedelight.shared.db.NoteDAO
import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.presentation.MainDispatcherRule
import com.softartdev.notedelight.shared.usecase.note.SaveNoteUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class SaveViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockRouter = Mockito.mock(Router::class.java)
    private val mockNoteDAO = Mockito.mock(NoteDAO::class.java)
    private lateinit var saveNoteUseCase: SaveNoteUseCase
    private val coroutineDispatchers = CoroutineDispatchersStub(
        scheduler = mainDispatcherRule.testDispatcher.scheduler
    )
    private lateinit var saveViewModel: SaveViewModel

    @Before
    fun setUp() = runTest(context = coroutineDispatchers.default) {
        Napier.base(PrintAntilog())
        saveNoteUseCase = SaveNoteUseCase(mockNoteDAO)
        saveViewModel = SaveViewModel(mockRouter, coroutineDispatchers)
    }

    @After
    fun tearDown() = Napier.takeLogarithm()

    @Test
    fun `save and nav back`() = runTest(timeout = 3.seconds) {
        val deferred: Deferred<Boolean?> = async { SaveNoteUseCase.dialogChannel.receive() }
        saveViewModel.saveNoteAndNavBack()
        advanceUntilIdle()
        Mockito.verify(mockRouter).popBackStack()
        assertTrue(deferred.await()!!)
        Mockito.verifyNoMoreInteractions(mockRouter)
    }

    @Test
    fun `don't save and nav back`() = runTest(timeout = 3.seconds) {
        val deferred: Deferred<Boolean?> = async { SaveNoteUseCase.dialogChannel.receive() }
        saveViewModel.doNotSaveAndNavBack()
        advanceUntilIdle()
        Mockito.verify(mockRouter).popBackStack()
        assertFalse(deferred.await()!!)
        Mockito.verifyNoMoreInteractions(mockRouter)
    }

    @Test
    fun `navigate up`() = runTest(timeout = 3.seconds) {
        val deferred: Deferred<Boolean?> = async { SaveNoteUseCase.dialogChannel.receive() }
        saveViewModel.navigateUp()
        advanceUntilIdle()
        Mockito.verify(mockRouter).popBackStack()
        assertNull(deferred.await())
        Mockito.verifyNoMoreInteractions(mockRouter)
    }
}