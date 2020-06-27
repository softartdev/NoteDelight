package com.softartdev.notedelight.ui.splash

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.shared.data.SafeSQLiteException
import com.softartdev.notedelight.shared.test.util.MainCoroutineRule
import com.softartdev.notedelight.shared.test.util.getOrAwaitValue
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
    fun navSignIn() {
        Mockito.`when`(cryptUseCase.dbIsEncrypted()).thenReturn(true)
        val splashViewModel = SplashViewModel(cryptUseCase)
        assertEquals(splashViewModel.resultLiveData.getOrAwaitValue(), SplashResult.NavSignIn)
    }

    @Test
    fun navMain() {
        Mockito.`when`(cryptUseCase.dbIsEncrypted()).thenReturn(false)
        val splashViewModel = SplashViewModel(cryptUseCase)
        assertEquals(splashViewModel.resultLiveData.getOrAwaitValue(), SplashResult.NavMain)
    }

    @Test
    fun showError() {
        Mockito.`when`(cryptUseCase.dbIsEncrypted()).thenThrow(SafeSQLiteException::class.java)
        val splashViewModel = SplashViewModel(cryptUseCase)
        assertEquals(splashViewModel.resultLiveData.getOrAwaitValue(), SplashResult.ShowError(null))
    }
}