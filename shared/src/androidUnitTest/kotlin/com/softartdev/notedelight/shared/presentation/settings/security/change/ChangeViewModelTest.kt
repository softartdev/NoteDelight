package com.softartdev.notedelight.shared.presentation.settings.security.change

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.shared.CoroutineDispatchersStub
import com.softartdev.notedelight.shared.presentation.MainDispatcherRule
import com.softartdev.notedelight.shared.StubEditable
import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.usecase.crypt.ChangePasswordUseCase
import com.softartdev.notedelight.shared.usecase.crypt.CheckPasswordUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
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

    private val checkPasswordUseCase = Mockito.mock(CheckPasswordUseCase::class.java)
    private val changePasswordUseCase = Mockito.mock(ChangePasswordUseCase::class.java)
    private val router = Mockito.mock(Router::class.java)
    private val coroutineDispatchers = CoroutineDispatchersStub(
        scheduler = mainDispatcherRule.testDispatcher.scheduler
    )
    private val changeViewModel = ChangeViewModel(checkPasswordUseCase, changePasswordUseCase, router, coroutineDispatchers)

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
            Mockito.`when`(checkPasswordUseCase(old)).thenReturn(true)
            val new = StubEditable("new")
            changeViewModel.checkChange(old, new, new)
            advanceUntilIdle()
            assertEquals(ChangeResult.Loading, awaitItem())

            Mockito.verify(router).popBackStack()
            Mockito.verifyNoMoreInteractions(router)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun checkChangeIncorrectPasswordError() = runTest {
        changeViewModel.resultStateFlow.test {
            assertEquals(ChangeResult.InitState, awaitItem())

            val old = StubEditable("old")
            Mockito.`when`(checkPasswordUseCase(old)).thenReturn(false)
            val new = StubEditable("new")
            changeViewModel.checkChange(old, new, new)
            assertEquals(ChangeResult.Loading, awaitItem())
            assertEquals(ChangeResult.IncorrectPasswordError, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}