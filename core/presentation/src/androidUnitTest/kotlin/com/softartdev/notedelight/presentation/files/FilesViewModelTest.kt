package com.softartdev.notedelight.presentation.files

import app.cash.turbine.test
import com.softartdev.notedelight.presentation.MainDispatcherRule
import com.softartdev.notedelight.repository.FileRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals
import kotlin.test.assertIs

@ExperimentalCoroutinesApi
class FilesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockFileRepo = Mockito.mock(FileRepo::class.java)
    private lateinit var filesViewModel: FilesViewModel

    @Before
    fun setUp() {
        filesViewModel = FilesViewModel(mockFileRepo)
    }

    @After
    fun tearDown() {
        Mockito.reset(mockFileRepo)
    }

    @Test
    fun updateFilesEmitsSuccess() = runTest {
        val fileFlow = MutableSharedFlow<List<String>>()
        Mockito.`when`(mockFileRepo.fileListFlow).thenReturn(fileFlow)

        filesViewModel.resultStateFlow.test {
            assertIs<FilesResult.Loading>(awaitItem())
            filesViewModel.updateFiles()
            fileFlow.emit(listOf("alpha", "beta"))
            val success = assertIs<FilesResult.Success>(awaitItem())
            assertEquals(listOf("alpha", "beta"), success.result)
            cancelAndIgnoreRemainingEvents()
        }
        Mockito.verify(mockFileRepo).goToStartPath()
    }

    @Test
    fun updateFilesReportsErrorWhenStartPathFails() = runTest {
        Mockito.`when`(mockFileRepo.fileListFlow).thenReturn(emptyFlow())
        Mockito.doThrow(RuntimeException("boom")).`when`(mockFileRepo).goToStartPath()

        filesViewModel.resultStateFlow.test {
            assertIs<FilesResult.Loading>(awaitItem())
            filesViewModel.updateFiles()
            val error = awaitItem()
            assertIs<FilesResult.Error>(error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onItemClickedReportsErrorOnFailure() = runTest {
        val errorMessage = "cannot open"
        Mockito.doThrow(IllegalStateException(errorMessage)).`when`(mockFileRepo).goTo("secret.txt")

        filesViewModel.resultStateFlow.test {
            assertIs<FilesResult.Loading>(awaitItem())
            filesViewModel.onItemClicked("secret.txt")
            val error = assertIs<FilesResult.Error>(awaitItem())
            assertEquals(errorMessage, error.error)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
