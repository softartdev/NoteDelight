package com.softartdev.notedelight.presentation.settings.security.confirm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.CoroutineDispatchersStub
import com.softartdev.notedelight.PrintAntilog
import com.softartdev.notedelight.interactor.SnackbarInteractor
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.presentation.MainDispatcherRule
import com.softartdev.notedelight.presentation.settings.security.FieldLabel
import com.softartdev.notedelight.usecase.crypt.ChangePasswordUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.verify

@OptIn(ExperimentalCoroutinesApi::class)
class ConfirmViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockChangePasswordUseCase = Mockito.mock(ChangePasswordUseCase::class.java)
    private val mockRouter = Mockito.mock(Router::class.java)
    private val mockSnackbarInteractor = Mockito.mock(SnackbarInteractor::class.java)
    private val coroutineDispatchers = CoroutineDispatchersStub(
        scheduler = mainDispatcherRule.testDispatcher.scheduler
    )
    private val viewModel = ConfirmViewModel(
        changePasswordUseCase = mockChangePasswordUseCase,
        snackbarInteractor = mockSnackbarInteractor,
        router = mockRouter,
        coroutineDispatchers = coroutineDispatchers
    )

    @Before
    fun setUp() = Napier.base(PrintAntilog())

    @After
    fun tearDown() {
        Napier.takeLogarithm()
        Mockito.reset(mockChangePasswordUseCase, mockSnackbarInteractor, mockRouter)
    }

    @Test
    fun `initial state`() = runTest {
        viewModel.stateFlow.test {
            val initialState = awaitItem()
            assertFalse(initialState.loading)
            assertTrue(initialState.password.isEmpty())
            assertTrue(initialState.repeatPassword.isEmpty())
            assertFalse(initialState.isPasswordError)
            assertFalse(initialState.isRepeatPasswordError)
            assertEquals(FieldLabel.ENTER_PASSWORD, initialState.passwordFieldLabel)
            assertEquals(FieldLabel.CONFIRM_PASSWORD, initialState.repeatPasswordFieldLabel)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `confirm success`() = runTest {
        viewModel.stateFlow.test {
            val initialState = awaitItem()

            val password = "password"
            viewModel.onAction(ConfirmAction.OnEditPassword(password))
            var state = awaitItem()
            assertEquals(password, state.password)
            assertEquals(FieldLabel.ENTER_PASSWORD, state.passwordFieldLabel)
            assertEquals(FieldLabel.CONFIRM_PASSWORD, state.repeatPasswordFieldLabel)
            assertFalse(state.isPasswordError)
            assertFalse(state.isRepeatPasswordError)

            viewModel.onAction(ConfirmAction.OnEditRepeatPassword(password))
            state = awaitItem()
            assertEquals(password, state.repeatPassword)
            assertFalse(state.isPasswordError)
            assertFalse(state.isRepeatPasswordError)

            viewModel.onAction(ConfirmAction.OnConfirmClick)
            state = awaitItem()
            assertTrue(state.loading)

            verify(mockRouter).popBackStack()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `empty password error`() = runTest {
        viewModel.stateFlow.test {
            val initialState = awaitItem()

            viewModel.onAction(ConfirmAction.OnEditRepeatPassword("password")) // Only set repeat password
            var state = awaitItem()

            viewModel.onAction(ConfirmAction.OnConfirmClick)
            state = awaitItem()
            assertTrue(state.loading)

            state = awaitItem()
            assertEquals(FieldLabel.ENTER_PASSWORD, state.passwordFieldLabel)
            assertEquals(FieldLabel.PASSWORDS_NOT_MATCH, state.repeatPasswordFieldLabel)
            assertTrue(state.isRepeatPasswordError)
            assertFalse(state.isPasswordError)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `passwords do not match error`() = runTest {
        viewModel.stateFlow.test {
            val initialState = awaitItem()

            // Enter different passwords
            viewModel.onAction(ConfirmAction.OnEditPassword("password1"))
            var state = awaitItem()
            assertFalse(state.isPasswordError)
            assertFalse(state.isRepeatPasswordError)

            viewModel.onAction(ConfirmAction.OnEditRepeatPassword("password2"))
            state = awaitItem()
            assertFalse(state.isPasswordError)
            assertFalse(state.isRepeatPasswordError)

            viewModel.onAction(ConfirmAction.OnConfirmClick)
            state = awaitItem()
            assertTrue(state.loading)

            state = awaitItem()
            assertEquals(FieldLabel.ENTER_PASSWORD, state.passwordFieldLabel)
            assertEquals(FieldLabel.PASSWORDS_NOT_MATCH, state.repeatPasswordFieldLabel)
            assertFalse(state.isPasswordError)
            assertTrue(state.isRepeatPasswordError)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `edit clears errors`() = runTest {
        viewModel.stateFlow.test {
            var state = awaitItem()
            viewModel.onAction(ConfirmAction.OnConfirmClick) // Trigger empty password error

            state = awaitItem() // Loading
            assertTrue(state.loading)

            state = awaitItem() // Error state
            assertTrue(state.isPasswordError)
            assertEquals(FieldLabel.EMPTY_PASSWORD, state.passwordFieldLabel)

            state = awaitItem()
            assertFalse(state.loading)

            viewModel.onAction(ConfirmAction.OnEditPassword("password"))

            state = awaitItem()
            assertFalse(state.isPasswordError)
            assertFalse(state.isRepeatPasswordError)
            assertEquals(FieldLabel.ENTER_PASSWORD, state.passwordFieldLabel)
            assertEquals(FieldLabel.CONFIRM_PASSWORD, state.repeatPasswordFieldLabel)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `cancel navigation`() = runTest {
        viewModel.stateFlow.test {
            val initialState = awaitItem()

            viewModel.onAction(ConfirmAction.Cancel)
            verify(mockRouter).popBackStack()

            cancelAndIgnoreRemainingEvents()
        }
    }
}
