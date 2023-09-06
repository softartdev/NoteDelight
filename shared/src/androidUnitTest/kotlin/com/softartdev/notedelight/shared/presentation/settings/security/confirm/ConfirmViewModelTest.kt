package com.softartdev.notedelight.shared.presentation.settings.security.confirm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.shared.presentation.MainDispatcherRule
import com.softartdev.notedelight.shared.StubEditable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class ConfirmViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val cryptUseCase = Mockito.mock(CryptUseCase::class.java)
    private val confirmViewModel = ConfirmViewModel(cryptUseCase)

    @Test
    fun conformCheckPasswordsNoMatchError() = runTest {
        confirmViewModel.resultStateFlow.test {
            assertEquals(ConfirmResult.InitState, awaitItem())

            confirmViewModel.conformCheck(StubEditable("pass"), StubEditable("new pass"))
            assertEquals(ConfirmResult.Loading, awaitItem())
            assertEquals(ConfirmResult.PasswordsNoMatchError, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun conformCheckEmptyPasswordError() = runTest {
        confirmViewModel.resultStateFlow.test {
            assertEquals(ConfirmResult.InitState, awaitItem())

            confirmViewModel.conformCheck(StubEditable(""), StubEditable(""))
            assertEquals(ConfirmResult.Loading, awaitItem())
            assertEquals(ConfirmResult.EmptyPasswordError, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun conformCheckSuccess() = runTest {
        confirmViewModel.resultStateFlow.test {
            assertEquals(ConfirmResult.InitState, awaitItem())

            confirmViewModel.conformCheck(StubEditable("pass"), StubEditable("pass"))
            assertEquals(ConfirmResult.Loading, awaitItem())
            assertEquals(ConfirmResult.Success, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun errorResult() {
        assertEquals(ConfirmResult.Error("err"), confirmViewModel.errorResult(Throwable("err")))
    }
}