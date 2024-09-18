@file:OptIn(ExperimentalCoroutinesApi::class)

package com.softartdev.notedelight.shared.presentation.note

import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.presentation.MainDispatcherRule
import com.softartdev.notedelight.shared.usecase.note.SaveNoteUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private val saveViewModel: SaveViewModel = SaveViewModel(mockRouter)

    @Test
    fun `save and nav back`() = runTest {
        saveViewModel.saveNoteAndNavBack()
        assertTrue(SaveNoteUseCase.saveChannel.receive())
        Mockito.verify(mockRouter).popBackStack()
    }

    @Test
    fun `Don't save and nav back`() = runTest {
        saveViewModel.doNotSaveAndNavBack()
        assertFalse(SaveNoteUseCase.saveChannel.receive())
        Mockito.verify(mockRouter).popBackStack()
    }

    @Test
    fun `navigate up`() = runTest {
        saveViewModel.navigateUp()
        Mockito.verify(mockRouter).popBackStack()
    }
}