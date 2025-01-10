package com.softartdev.notedelight.presentation.settings.security.enter

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
class EnterViewModelTest {

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
    private val viewModel = EnterViewModel(
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
            assertFalse(initialState.loading)
            assertTrue(initialState.password.isEmpty())
            assertFalse(initialState.isPasswordVisible)
            assertFalse(initialState.isError)
            assertNull(initialState.snackBarMessageType)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `enter password success`() = runTest {
        viewModel.stateFlow.test {
            val initialState = awaitItem()

            val password = "password"
            Mockito.`when`(mockCheckPasswordUseCase(password)).thenReturn(true)

            initialState.onEditPassword(password)
            val editedState = awaitItem()
            assertEquals(password, editedState.password)
            assertFalse(editedState.isError)

            editedState.onEnterClick()
            val loadingState = awaitItem()
            assertTrue(loadingState.loading)

            verify(mockRouter).popBackStack()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `empty password error`() = runTest {
        viewModel.stateFlow.test {
            var resultState = awaitItem()
            assertFalse(resultState.loading)

            resultState.onEnterClick()
            resultState = awaitItem()
            assertTrue(resultState.loading)

            resultState = awaitItem()
            assertEquals(FieldLabel.EMPTY, resultState.fieldLabel)

            resultState = awaitItem()
            assertTrue(resultState.isError)

            resultState = awaitItem()
            assertFalse(resultState.loading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `incorrect password error`() = runTest {
        val password = "wrong"
        Mockito.`when`(mockCheckPasswordUseCase(password)).thenReturn(false)
        viewModel.stateFlow.test {
            var resultState = awaitItem()
            assertFalse(resultState.loading)
            assertFalse(resultState.isError)
            assertEquals(FieldLabel.ENTER, resultState.fieldLabel)

            resultState.onEditPassword(password)
            resultState = awaitItem()

            resultState.onEnterClick()
            resultState = awaitItem()
            assertTrue(resultState.loading)

            resultState = awaitItem()
            assertEquals(FieldLabel.INCORRECT, resultState.fieldLabel)

            resultState = awaitItem()
            assertTrue(resultState.isError)

            resultState = awaitItem()
            assertFalse(resultState.loading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggle password visibility`() = runTest {
        viewModel.stateFlow.test {
            val initialState = awaitItem()
            assertFalse(initialState.isPasswordVisible)

            initialState.onTogglePasswordVisibility()
            val toggledState = awaitItem()
            assertTrue(toggledState.isPasswordVisible)

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
