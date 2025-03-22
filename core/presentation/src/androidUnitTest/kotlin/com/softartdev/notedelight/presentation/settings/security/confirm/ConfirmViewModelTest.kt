package com.softartdev.notedelight.presentation.settings.security.confirm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.CoroutineDispatchersStub
import com.softartdev.notedelight.PrintAntilog
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
import org.junit.Assert.assertNull
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
    private val coroutineDispatchers = CoroutineDispatchersStub(
        scheduler = mainDispatcherRule.testDispatcher.scheduler
    )
    private val viewModel = ConfirmViewModel(
        changePasswordUseCase = mockChangePasswordUseCase,
        router = mockRouter,
        coroutineDispatchers = coroutineDispatchers
    )

    @Before
    fun setUp() = Napier.base(PrintAntilog())

    @After
    fun tearDown() {
        Napier.takeLogarithm()
        Mockito.reset(mockChangePasswordUseCase, mockRouter)
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
            assertEquals(FieldLabel.ENTER, initialState.passwordFieldLabel)
            assertEquals(FieldLabel.ENTER, initialState.repeatPasswordFieldLabel)
            assertNull(initialState.snackBarMessageType)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `confirm success`() = runTest {
        viewModel.stateFlow.test {
            val initialState = awaitItem()

            val password = "password"
            initialState.onEditPassword(password)
            var state = awaitItem()
            assertEquals(password, state.password)
            assertEquals(FieldLabel.ENTER, state.passwordFieldLabel)
            assertEquals(FieldLabel.ENTER, state.repeatPasswordFieldLabel)
            assertFalse(state.isPasswordError)
            assertFalse(state.isRepeatPasswordError)

            state.onEditRepeatPassword(password)
            state = awaitItem()
            assertEquals(password, state.repeatPassword)
            assertFalse(state.isPasswordError)
            assertFalse(state.isRepeatPasswordError)

            state.onConfirmClick()
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

            initialState.onEditRepeatPassword("password") // Only set repeat password
            var state = awaitItem()

            state.onConfirmClick()
            state = awaitItem()
            assertTrue(state.loading)

            state = awaitItem()
            assertEquals(FieldLabel.ENTER, state.passwordFieldLabel)
            assertEquals(FieldLabel.NO_MATCH, state.repeatPasswordFieldLabel)
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
            initialState.onEditPassword("password1")
            var state = awaitItem()
            assertFalse(state.isPasswordError)
            assertFalse(state.isRepeatPasswordError)

            state.onEditRepeatPassword("password2")
            state = awaitItem()
            assertFalse(state.isPasswordError)
            assertFalse(state.isRepeatPasswordError)

            state.onConfirmClick()
            state = awaitItem()
            assertTrue(state.loading)

            state = awaitItem()
            assertEquals(FieldLabel.ENTER, state.passwordFieldLabel)
            assertEquals(FieldLabel.NO_MATCH, state.repeatPasswordFieldLabel)
            assertFalse(state.isPasswordError)
            assertTrue(state.isRepeatPasswordError)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `edit clears errors`() = runTest {
        viewModel.stateFlow.test {
            var state = awaitItem()
            state.onConfirmClick() // Trigger empty password error

            state = awaitItem() // Loading
            assertTrue(state.loading)

            state = awaitItem() // Error state
            assertTrue(state.isPasswordError)
            assertEquals(FieldLabel.EMPTY, state.passwordFieldLabel)

            state = awaitItem()
            assertFalse(state.loading)

            state.onEditPassword("password")

            state = awaitItem()
            assertFalse(state.isPasswordError)
            assertFalse(state.isRepeatPasswordError)
            assertEquals(FieldLabel.ENTER, state.passwordFieldLabel)
            assertEquals(FieldLabel.ENTER, state.repeatPasswordFieldLabel)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `cancel navigation`() = runTest {
        viewModel.stateFlow.test {
            val initialState = awaitItem()

            initialState.onCancel()
            verify(mockRouter).popBackStack()

            cancelAndIgnoreRemainingEvents()
        }
    }
}
