package com.softartdev.notedelight.shared.presentation.settings.security.change

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.shared.test.util.MainDispatcherRule
import com.softartdev.notedelight.shared.test.util.StubEditable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class ChangeViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val cryptUseCase = Mockito.mock(CryptUseCase::class.java)
    private val changeViewModel = ChangeViewModel(cryptUseCase)

    @Test
    fun checkChangeOldEmptyPasswordError() = runTest {
        changeViewModel.resultStateFlow.test {
            assertEquals(ChangeResult.InitState, awaitItem())

            val old = StubEditable("")
            val new = StubEditable("new")
            changeViewModel.checkChange(old, new, new)
            assertEquals(ChangeResult.Loading, awaitItem())
            assertEquals(ChangeResult.OldEmptyPasswordError, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun checkChangeNewEmptyPasswordError() = runTest {
        changeViewModel.resultStateFlow.test {
            assertEquals(ChangeResult.InitState, awaitItem())

            val old = StubEditable("old")
            val new = StubEditable("")
            changeViewModel.checkChange(old, new, new)
            assertEquals(ChangeResult.Loading, awaitItem())
            assertEquals(ChangeResult.NewEmptyPasswordError, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun checkChangePasswordsNoMatchError() = runTest {
        changeViewModel.resultStateFlow.test {
            assertEquals(ChangeResult.InitState, awaitItem())

            val old = StubEditable("old")
            val new = StubEditable("new")
            val rep = StubEditable("rep")
            changeViewModel.checkChange(old, new, rep)
            assertEquals(ChangeResult.Loading, awaitItem())
            assertEquals(ChangeResult.PasswordsNoMatchError, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun checkChangeSuccess() = runTest {
        changeViewModel.resultStateFlow.test {
            assertEquals(ChangeResult.InitState, awaitItem())

            val old = StubEditable("old")
            Mockito.`when`(cryptUseCase.checkPassword(old)).thenReturn(true)
            val new = StubEditable("new")
            changeViewModel.checkChange(old, new, new)
            assertEquals(ChangeResult.Loading, awaitItem())
            assertEquals(ChangeResult.Success, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun checkChangeIncorrectPasswordError() = runTest {
        changeViewModel.resultStateFlow.test {
            assertEquals(ChangeResult.InitState, awaitItem())

            val old = StubEditable("old")
            Mockito.`when`(cryptUseCase.checkPassword(old)).thenReturn(false)
            val new = StubEditable("new")
            changeViewModel.checkChange(old, new, new)
            assertEquals(ChangeResult.Loading, awaitItem())
            assertEquals(ChangeResult.IncorrectPasswordError, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun errorResult() {
        assertEquals(ChangeResult.Error("err"), changeViewModel.errorResult(Throwable("err")))
    }
}