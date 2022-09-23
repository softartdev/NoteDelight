package com.softartdev.notedelight.shared.presentation.splash

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.shared.test.util.MainDispatcherRule
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

    private val cryptUseCase = Mockito.mock(CryptUseCase::class.java)

    @Test
    fun navSignIn() = runTest {
        Mockito.`when`(cryptUseCase.dbIsEncrypted()).thenReturn(true)
        val splashViewModel = SplashViewModel(cryptUseCase)
        splashViewModel.resultStateFlow.test {
            assertEquals(SplashResult.Loading, awaitItem())

            splashViewModel.checkEncryption()
            assertEquals(SplashResult.NavSignIn, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun navMain() = runTest {
        Mockito.`when`(cryptUseCase.dbIsEncrypted()).thenReturn(false)
        val splashViewModel = SplashViewModel(cryptUseCase)
        splashViewModel.resultStateFlow.test {
            assertEquals(SplashResult.Loading, awaitItem())

            splashViewModel.checkEncryption()
            assertEquals(SplashResult.NavMain, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun showError() = runTest {
        Mockito.`when`(cryptUseCase.dbIsEncrypted()).thenThrow(RuntimeException::class.java)
        val splashViewModel = SplashViewModel(cryptUseCase)
        splashViewModel.resultStateFlow.test {
            assertEquals(SplashResult.Loading, awaitItem())

            splashViewModel.checkEncryption()
            assertEquals(SplashResult.ShowError(null), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}