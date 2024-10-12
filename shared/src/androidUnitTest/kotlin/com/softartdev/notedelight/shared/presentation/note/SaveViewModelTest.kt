@file:OptIn(ExperimentalCoroutinesApi::class)

package com.softartdev.notedelight.shared.presentation.note

import com.softartdev.notedelight.shared.CoroutineDispatchersStub
import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.presentation.MainDispatcherRule
import com.softartdev.notedelight.shared.usecase.note.SaveNoteUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SaveViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockRouter = Mockito.mock(Router::class.java)
    private val coroutineDispatchers = CoroutineDispatchersStub(
        scheduler = mainDispatcherRule.testDispatcher.scheduler
    )
    private val saveViewModel: SaveViewModel = SaveViewModel(mockRouter, coroutineDispatchers)

    @Test
    fun `Don't save and nav back`() = runTest {
        saveViewModel.doNotSaveAndNavBack()
        advanceUntilIdle()
        assertFalse(SaveNoteUseCase.saveChannel.receiveCatching().getOrThrow())
        advanceUntilIdle()
        Mockito.verify(mockRouter).popBackStack()
        Mockito.verifyNoMoreInteractions(mockRouter)
    }

    @Test
    fun `save and nav back`() = runTest {
        saveViewModel.saveNoteAndNavBack()
        advanceUntilIdle()
        assertTrue(SaveNoteUseCase.saveChannel.receiveCatching().getOrThrow())
        advanceUntilIdle()
        Mockito.verify(mockRouter).popBackStack()
        Mockito.verifyNoMoreInteractions(mockRouter)
    }

    @Test
    fun `navigate up`() = runTest {
        saveViewModel.navigateUp()
        advanceUntilIdle()
        Mockito.verify(mockRouter).popBackStack()
        Mockito.verifyNoMoreInteractions(mockRouter)
    }
}