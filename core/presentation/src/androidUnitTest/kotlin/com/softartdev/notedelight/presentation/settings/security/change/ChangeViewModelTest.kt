package com.softartdev.notedelight.presentation.settings.security.change

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.CoroutineDispatchersStub
import com.softartdev.notedelight.PrintAntilog
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.presentation.MainDispatcherRule
import com.softartdev.notedelight.presentation.settings.security.FieldLabel
import com.softartdev.notedelight.usecase.crypt.ChangePasswordUseCase
import com.softartdev.notedelight.usecase.crypt.CheckPasswordUseCase
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
class ChangeViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockCheckPasswordUseCase = Mockito.mock(CheckPasswordUseCase::class.java)
    private val mockChangePasswordUseCase = Mockito.mock(ChangePasswordUseCase::class.java)
    private val mockRouter = Mockito.mock(Router::class.java)
    private val coroutineDispatchers = CoroutineDispatchersStub(
        scheduler = mainDispatcherRule.testDispatcher.scheduler
    )
    private val viewModel = ChangeViewModel(
        checkPasswordUseCase = mockCheckPasswordUseCase,
        changePasswordUseCase = mockChangePasswordUseCase,
        router = mockRouter,
        coroutineDispatchers = coroutineDispatchers
    )

    @Before
    fun setUp() = Napier.base(PrintAntilog())

    @After
    fun tearDown() {
        Napier.takeLogarithm()
        Mockito.reset(mockCheckPasswordUseCase, mockChangePasswordUseCase, mockRouter)
    }

    @Test
    fun `initial state`() = runTest {
        viewModel.stateFlow.test {
            val initialState = awaitItem()
            // Check initial values
            assertFalse(initialState.loading)
            assertTrue(initialState.oldPassword.isEmpty())
            assertTrue(initialState.newPassword.isEmpty())
            assertTrue(initialState.repeatNewPassword.isEmpty())
            // Check initial errors state
            assertFalse(initialState.isOldPasswordError)
            assertFalse(initialState.isNewPasswordError)
            assertFalse(initialState.isRepeatPasswordError)
            // Check initial label types
            assertEquals(FieldLabel.ENTER_OLD_PASSWORD, initialState.oldPasswordFieldLabel)
            assertEquals(FieldLabel.ENTER_NEW_PASSWORD, initialState.newPasswordFieldLabel)
            assertEquals(FieldLabel.REPEAT_NEW_PASSWORD, initialState.repeatPasswordFieldLabel)
            assertNull(initialState.snackBarMessageType)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `change password success`() = runTest {
        val oldPassword = "old"
        val newPassword = "new"
        Mockito.`when`(mockCheckPasswordUseCase(oldPassword)).thenReturn(true)

        viewModel.stateFlow.test {
            var state = awaitItem() // Initial state

            viewModel.onAction(ChangeAction.OnEditOldPassword(oldPassword))
            state = awaitItem()
            assertEquals(oldPassword, state.oldPassword)

            viewModel.onAction(ChangeAction.OnEditNewPassword(newPassword))
            state = awaitItem()
            assertEquals(newPassword, state.newPassword)

            viewModel.onAction(ChangeAction.OnEditRepeatPassword(newPassword))
            state = awaitItem()
            assertEquals(newPassword, state.repeatNewPassword)

            viewModel.onAction(ChangeAction.OnChangeClick)
            state = awaitItem() // Loading state
            assertTrue(state.loading)

            verify(mockRouter).popBackStack()
            verify(mockChangePasswordUseCase).invoke(oldPassword, newPassword)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `empty old password error`() = runTest {
        viewModel.stateFlow.test {
            var state = awaitItem()
            // Set only new passwords
            viewModel.onAction(ChangeAction.OnEditNewPassword("new"))
            state = awaitItem()
            viewModel.onAction(ChangeAction.OnEditRepeatPassword("new"))
            state = awaitItem()

            viewModel.onAction(ChangeAction.OnChangeClick)
            state = awaitItem() // Loading
            assertTrue(state.loading)

            state = awaitItem() // Error state
            assertEquals(FieldLabel.EMPTY_PASSWORD, state.oldPasswordFieldLabel)
            assertTrue(state.isOldPasswordError)
            assertFalse(state.isNewPasswordError)
            assertFalse(state.isRepeatPasswordError)

            state = awaitItem() // Loading finished
            assertFalse(state.loading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `empty new password error`() = runTest {
        viewModel.stateFlow.test {
            var state = awaitItem()
            // Set only old password
            viewModel.onAction(ChangeAction.OnEditOldPassword("old"))
            state = awaitItem()

            viewModel.onAction(ChangeAction.OnChangeClick)
            state = awaitItem() // Loading
            assertTrue(state.loading)

            state = awaitItem() // Error state
            assertEquals(FieldLabel.EMPTY_PASSWORD, state.newPasswordFieldLabel)
            assertFalse(state.isOldPasswordError)
            assertTrue(state.isNewPasswordError)
            assertFalse(state.isRepeatPasswordError)

            state = awaitItem() // Loading finished
            assertFalse(state.loading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `passwords do not match error`() = runTest {
        viewModel.stateFlow.test {
            var state = awaitItem()

            viewModel.onAction(ChangeAction.OnEditOldPassword("old"))
            state = awaitItem()
            viewModel.onAction(ChangeAction.OnEditNewPassword("new1"))
            state = awaitItem()
            viewModel.onAction(ChangeAction.OnEditRepeatPassword("new2"))
            state = awaitItem()

            viewModel.onAction(ChangeAction.OnChangeClick)
            state = awaitItem() // Loading
            assertTrue(state.loading)

            state = awaitItem() // Error state
            assertEquals(FieldLabel.PASSWORDS_NOT_MATCH, state.repeatPasswordFieldLabel)
            assertFalse(state.isOldPasswordError)
            assertFalse(state.isNewPasswordError)
            assertTrue(state.isRepeatPasswordError)

            state = awaitItem() // Loading finished
            assertFalse(state.loading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `incorrect old password error`() = runTest {
        val oldPassword = "wrong"
        Mockito.`when`(mockCheckPasswordUseCase(oldPassword)).thenReturn(false)

        viewModel.stateFlow.test {
            var state = awaitItem()

            viewModel.onAction(ChangeAction.OnEditOldPassword(oldPassword))
            state = awaitItem()
            viewModel.onAction(ChangeAction.OnEditNewPassword("new"))
            state = awaitItem()
            viewModel.onAction(ChangeAction.OnEditRepeatPassword("new"))
            state = awaitItem()

            viewModel.onAction(ChangeAction.OnChangeClick)
            state = awaitItem() // Loading
            assertTrue(state.loading)

            state = awaitItem() // Error state
            assertEquals(FieldLabel.INCORRECT_PASSWORD, state.oldPasswordFieldLabel)
            assertTrue(state.isOldPasswordError)
            assertFalse(state.isNewPasswordError)
            assertFalse(state.isRepeatPasswordError)

            state = awaitItem() // Loading finished
            assertFalse(state.loading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `edit clears errors`() = runTest {
        viewModel.stateFlow.test {
            var state = awaitItem()
            viewModel.onAction(ChangeAction.OnChangeClick) // Trigger empty old password error
            state = awaitItem() // Loading
            assertTrue(state.loading)
            state = awaitItem() // Error state
            assertTrue(state.isOldPasswordError)
            state = awaitItem() // Loading finished

            viewModel.onAction(ChangeAction.OnEditOldPassword("old"))
            state = awaitItem()
            assertFalse(state.isOldPasswordError)
            assertFalse(state.isNewPasswordError)
            assertFalse(state.isRepeatPasswordError)
            assertEquals(FieldLabel.ENTER_OLD_PASSWORD, state.oldPasswordFieldLabel)
            assertEquals(FieldLabel.ENTER_NEW_PASSWORD, state.newPasswordFieldLabel)
            assertEquals(FieldLabel.REPEAT_NEW_PASSWORD, state.repeatPasswordFieldLabel)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `cancel navigation`() = runTest {
        viewModel.stateFlow.test {
            val initialState = awaitItem()

            viewModel.onAction(ChangeAction.Cancel)
            verify(mockRouter).popBackStack()

            cancelAndIgnoreRemainingEvents()
        }
    }
}