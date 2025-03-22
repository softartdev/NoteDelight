@file:OptIn(ExperimentalCoroutinesApi::class)

package com.softartdev.notedelight.presentation.note

import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.presentation.MainDispatcherRule
import com.softartdev.notedelight.usecase.note.DeleteNoteUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DeleteViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockRouter = Mockito.mock(Router::class.java)
    private val deleteViewModel: DeleteViewModel = DeleteViewModel(mockRouter)

    @Test
    fun deleteNoteAndNavBack() = runTest {
        deleteViewModel.deleteNoteAndNavBack()
        assertTrue(DeleteNoteUseCase.deleteChannel.receive())
        Mockito.verify(mockRouter).popBackStack()
    }

    @Test
    fun doNotDeleteAndNavBack() = runTest {
        deleteViewModel.doNotDeleteAndNavBack()
        assertFalse(DeleteNoteUseCase.deleteChannel.receive())
        Mockito.verify(mockRouter).popBackStack()
    }

    @Test
    fun navigateUp() = runTest {
        deleteViewModel.navigateUp()
        Mockito.verify(mockRouter).popBackStack()
    }
}