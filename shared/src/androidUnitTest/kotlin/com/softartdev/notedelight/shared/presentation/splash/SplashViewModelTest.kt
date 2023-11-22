package com.softartdev.notedelight.shared.presentation.splash

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.shared.PlatformSQLiteState
import com.softartdev.notedelight.shared.db.SafeRepo
import com.softartdev.notedelight.shared.presentation.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class SplashViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockSafeRepo = Mockito.mock(SafeRepo::class.java)

    @Test
    fun navSignIn() = runTest {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(PlatformSQLiteState.ENCRYPTED)
        val splashViewModel = SplashViewModel(mockSafeRepo)
        splashViewModel.resultStateFlow.test {
            assertEquals(SplashResult.Loading, awaitItem())

            splashViewModel.checkEncryption()
            assertEquals(SplashResult.NavSignIn, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun navMain() = runTest {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(PlatformSQLiteState.UNENCRYPTED)
        val splashViewModel = SplashViewModel(mockSafeRepo)
        splashViewModel.resultStateFlow.test {
            assertEquals(SplashResult.Loading, awaitItem())

            splashViewModel.checkEncryption()
            assertEquals(SplashResult.NavMain, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun showError() = runTest {
        Mockito.`when`(mockSafeRepo.databaseState).thenThrow(RuntimeException::class.java)
        val splashViewModel = SplashViewModel(mockSafeRepo)
        splashViewModel.resultStateFlow.test {
            assertEquals(SplashResult.Loading, awaitItem())

            splashViewModel.checkEncryption()
            assertEquals(SplashResult.ShowError(null), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}