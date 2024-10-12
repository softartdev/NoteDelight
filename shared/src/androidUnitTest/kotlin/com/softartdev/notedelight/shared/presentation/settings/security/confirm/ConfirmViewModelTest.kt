package com.softartdev.notedelight.shared.presentation.settings.security.confirm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.shared.CoroutineDispatchersStub
import com.softartdev.notedelight.shared.StubEditable
import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.presentation.MainDispatcherRule
import com.softartdev.notedelight.shared.usecase.crypt.ChangePasswordUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
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

    private val changePasswordUseCase = Mockito.mock(ChangePasswordUseCase::class.java)
    private val router = Mockito.mock(Router::class.java)
    private val coroutineDispatchers = CoroutineDispatchersStub(
        scheduler = mainDispatcherRule.testDispatcher.scheduler
    )
    private val confirmViewModel = ConfirmViewModel(changePasswordUseCase, router, coroutineDispatchers)

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
            advanceUntilIdle()
            assertEquals(ConfirmResult.Loading, awaitItem())

            Mockito.verify(router).popBackStack()
            Mockito.verifyNoMoreInteractions(router)

            cancelAndIgnoreRemainingEvents()
        }
    }
}