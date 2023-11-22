package com.softartdev.notedelight.shared.presentation.settings.security.enter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.shared.presentation.MainDispatcherRule
import com.softartdev.notedelight.shared.StubEditable
import com.softartdev.notedelight.shared.usecase.crypt.ChangePasswordUseCase
import com.softartdev.notedelight.shared.usecase.crypt.CheckPasswordUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class EnterViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val checkPasswordUseCase = Mockito.mock(CheckPasswordUseCase::class.java)
    private val changePasswordUseCase = Mockito.mock(ChangePasswordUseCase::class.java)
    private val enterViewModel = EnterViewModel(checkPasswordUseCase, changePasswordUseCase)

    @Test
    fun enterCheckSuccess() = runTest {
        enterViewModel.resultStateFlow.test {
            assertEquals(EnterResult.InitState, awaitItem())

            val pass = StubEditable("pass")
            Mockito.`when`(checkPasswordUseCase(pass)).thenReturn(true)
            enterViewModel.enterCheck(pass)
            assertEquals(EnterResult.Loading, awaitItem())
            assertEquals(EnterResult.Success, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun enterCheckIncorrectPasswordError() = runTest {
        enterViewModel.resultStateFlow.test {
            assertEquals(EnterResult.InitState, awaitItem())

            val pass = StubEditable("pass")
            Mockito.`when`(checkPasswordUseCase(pass)).thenReturn(false)
            enterViewModel.enterCheck(pass)
            assertEquals(EnterResult.Loading, awaitItem())
            assertEquals(EnterResult.IncorrectPasswordError, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun enterCheckEmptyPasswordError() = runTest {
        enterViewModel.resultStateFlow.test {
            assertEquals(EnterResult.InitState, awaitItem())

            enterViewModel.enterCheck(StubEditable(""))
            assertEquals(EnterResult.Loading, awaitItem())
            assertEquals(EnterResult.EmptyPasswordError, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun errorResult() {
        assertEquals(EnterResult.Error("err"), enterViewModel.errorResult(Throwable("err")))
    }
}