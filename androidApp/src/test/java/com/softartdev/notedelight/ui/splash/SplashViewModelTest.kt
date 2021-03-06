package com.softartdev.notedelight.ui.splash

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.shared.test.util.MainCoroutineRule
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class SplashViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val cryptUseCase = Mockito.mock(CryptUseCase::class.java)

    @Test
    fun navSignIn() = runBlocking {
        Mockito.`when`(cryptUseCase.dbIsEncrypted()).thenReturn(true)
        val splashViewModel = SplashViewModel(cryptUseCase)
        splashViewModel.resultStateFlow.test {
            assertEquals(SplashResult.Loading, expectItem())

            splashViewModel.checkEncryption()
            assertEquals(SplashResult.NavSignIn, expectItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun navMain() = runBlocking {
        Mockito.`when`(cryptUseCase.dbIsEncrypted()).thenReturn(false)
        val splashViewModel = SplashViewModel(cryptUseCase)
        splashViewModel.resultStateFlow.test {
            assertEquals(SplashResult.Loading, expectItem())

            splashViewModel.checkEncryption()
            assertEquals(SplashResult.NavMain, expectItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun showError() = runBlocking {
        Mockito.`when`(cryptUseCase.dbIsEncrypted()).thenThrow(RuntimeException::class.java)
        val splashViewModel = SplashViewModel(cryptUseCase)
        splashViewModel.resultStateFlow.test {
            assertEquals(SplashResult.Loading, expectItem())

            splashViewModel.checkEncryption()
            assertEquals(SplashResult.ShowError(null), expectItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}