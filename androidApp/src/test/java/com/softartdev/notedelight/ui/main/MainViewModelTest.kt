package com.softartdev.notedelight.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.softartdev.notedelight.shared.data.NoteUseCase
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.test.util.MainCoroutineRule
import com.softartdev.notedelight.shared.test.util.assertValues
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import net.sqlcipher.database.SQLiteException
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val noteUseCase = Mockito.mock(NoteUseCase::class.java)
    private val mainViewModel = MainViewModel(noteUseCase)

    @Test
    fun success() = mainCoroutineRule.runBlockingTest {
        val notes = emptyList<Note>()
        Mockito.`when`(noteUseCase.getNotes()).thenReturn(flowOf(notes))
        mainViewModel.resultLiveData.assertValues(
                NoteListResult.Loading,
                NoteListResult.Success(notes)
        ) {
            mainViewModel.updateNotes()
        }
    }

    @Test
    fun navMain() = mainCoroutineRule.runBlockingTest {
        Mockito.`when`(noteUseCase.getNotes()).thenReturn(flow { throw SQLiteException() })
        mainViewModel.resultLiveData.assertValues(
                NoteListResult.Loading,
                NoteListResult.NavMain
        ){
            mainViewModel.updateNotes()
        }
    }

    @Test
    fun error() = mainCoroutineRule.runBlockingTest {
        Mockito.`when`(noteUseCase.getNotes()).thenReturn(flow { throw Throwable() })
        mainViewModel.resultLiveData.assertValues(
                NoteListResult.Loading,
                NoteListResult.Error(null)
        ) {
            mainViewModel.updateNotes()
        }
    }
}