package com.softartdev.notedelight.ui.settings.security.change

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.shared.presentation.settings.security.change.ChangeResult
import com.softartdev.notedelight.shared.presentation.settings.security.change.ChangeViewModel
import com.softartdev.notedelight.shared.test.util.MainCoroutineRule
import com.softartdev.notedelight.shared.test.util.StubEditable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class ChangeViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val cryptUseCase = Mockito.mock(CryptUseCase::class.java)
    private val changeViewModel = ChangeViewModel(cryptUseCase)

    @Test
    fun checkChangeOldEmptyPasswordError() = runBlocking {
        changeViewModel.resultStateFlow.test {
            assertEquals(ChangeResult.InitState, expectItem())

            val old = StubEditable("")
            val new = StubEditable("new")
            changeViewModel.checkChange(old, new, new)
            assertEquals(ChangeResult.Loading, expectItem())
            assertEquals(ChangeResult.OldEmptyPasswordError, expectItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun checkChangeNewEmptyPasswordError() = runBlocking {
        changeViewModel.resultStateFlow.test {
            assertEquals(ChangeResult.InitState, expectItem())

            val old = StubEditable("old")
            val new = StubEditable("")
            changeViewModel.checkChange(old, new, new)
            assertEquals(ChangeResult.Loading, expectItem())
            assertEquals(ChangeResult.NewEmptyPasswordError, expectItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun checkChangePasswordsNoMatchError() = runBlocking {
        changeViewModel.resultStateFlow.test {
            assertEquals(ChangeResult.InitState, expectItem())

            val old = StubEditable("old")
            val new = StubEditable("new")
            val rep = StubEditable("rep")
            changeViewModel.checkChange(old, new, rep)
            assertEquals(ChangeResult.Loading, expectItem())
            assertEquals(ChangeResult.PasswordsNoMatchError, expectItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun checkChangeSuccess() = runBlocking {
        changeViewModel.resultStateFlow.test {
            assertEquals(ChangeResult.InitState, expectItem())

            val old = StubEditable("old")
            Mockito.`when`(cryptUseCase.checkPassword(old)).thenReturn(true)
            val new = StubEditable("new")
            changeViewModel.checkChange(old, new, new)
            assertEquals(ChangeResult.Loading, expectItem())
            assertEquals(ChangeResult.Success, expectItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun checkChangeIncorrectPasswordError() = runBlocking {
        changeViewModel.resultStateFlow.test {
            assertEquals(ChangeResult.InitState, expectItem())

            val old = StubEditable("old")
            Mockito.`when`(cryptUseCase.checkPassword(old)).thenReturn(false)
            val new = StubEditable("new")
            changeViewModel.checkChange(old, new, new)
            assertEquals(ChangeResult.Loading, expectItem())
            assertEquals(ChangeResult.IncorrectPasswordError, expectItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun errorResult() {
        assertEquals(ChangeResult.Error("err"), changeViewModel.errorResult(Throwable("err")))
    }
}