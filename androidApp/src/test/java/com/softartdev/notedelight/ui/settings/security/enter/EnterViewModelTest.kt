package com.softartdev.notedelight.ui.settings.security.enter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.shared.presentation.settings.security.enter.EnterResult
import com.softartdev.notedelight.shared.presentation.settings.security.enter.EnterViewModel
import com.softartdev.notedelight.shared.test.util.MainCoroutineRule
import com.softartdev.notedelight.shared.test.util.StubEditable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class EnterViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val cryptUseCase = Mockito.mock(CryptUseCase::class.java)
    private val enterViewModel = EnterViewModel(cryptUseCase)

    @Test
    fun enterCheckSuccess() = runBlocking {
        enterViewModel.resultStateFlow.test {
            assertEquals(EnterResult.InitState, expectItem())

            val pass = StubEditable("pass")
            Mockito.`when`(cryptUseCase.checkPassword(pass)).thenReturn(true)
            enterViewModel.enterCheck(pass)
            assertEquals(EnterResult.Loading, expectItem())
            assertEquals(EnterResult.Success, expectItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun enterCheckIncorrectPasswordError() = runBlocking {
        enterViewModel.resultStateFlow.test {
            assertEquals(EnterResult.InitState, expectItem())

            val pass = StubEditable("pass")
            Mockito.`when`(cryptUseCase.checkPassword(pass)).thenReturn(false)
            enterViewModel.enterCheck(pass)
            assertEquals(EnterResult.Loading, expectItem())
            assertEquals(EnterResult.IncorrectPasswordError, expectItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun enterCheckEmptyPasswordError() = runBlocking {
        enterViewModel.resultStateFlow.test {
            assertEquals(EnterResult.InitState, expectItem())

            enterViewModel.enterCheck(StubEditable(""))
            assertEquals(EnterResult.Loading, expectItem())
            assertEquals(EnterResult.EmptyPasswordError, expectItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun errorResult() {
        assertEquals(EnterResult.Error("err"), enterViewModel.errorResult(Throwable("err")))
    }
}