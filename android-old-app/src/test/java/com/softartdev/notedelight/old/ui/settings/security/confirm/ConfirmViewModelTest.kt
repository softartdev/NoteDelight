package com.softartdev.notedelight.old.ui.settings.security.confirm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.shared.presentation.settings.security.confirm.ConfirmResult
import com.softartdev.notedelight.shared.presentation.settings.security.confirm.ConfirmViewModel
import com.softartdev.notedelight.shared.test.util.MainCoroutineRule
import com.softartdev.notedelight.shared.test.util.StubEditable
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class ConfirmViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val cryptUseCase = Mockito.mock(CryptUseCase::class.java)
    private val confirmViewModel = ConfirmViewModel(cryptUseCase)

    @Test
    fun conformCheckPasswordsNoMatchError() = runBlocking {
        confirmViewModel.resultStateFlow.test {
            assertEquals(ConfirmResult.InitState, awaitItem())

            confirmViewModel.conformCheck(StubEditable("pass"), StubEditable("new pass"))
            assertEquals(ConfirmResult.Loading, awaitItem())
            assertEquals(ConfirmResult.PasswordsNoMatchError, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun conformCheckEmptyPasswordError() = runBlocking {
        confirmViewModel.resultStateFlow.test {
            assertEquals(ConfirmResult.InitState, awaitItem())

            confirmViewModel.conformCheck(StubEditable(""), StubEditable(""))
            assertEquals(ConfirmResult.Loading, awaitItem())
            assertEquals(ConfirmResult.EmptyPasswordError, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun conformCheckSuccess() = runBlocking {
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