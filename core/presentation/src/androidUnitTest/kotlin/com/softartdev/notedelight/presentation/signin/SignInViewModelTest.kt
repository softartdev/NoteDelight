package com.softartdev.notedelight.presentation.signin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.StubEditable
import com.softartdev.notedelight.anyObject
import com.softartdev.notedelight.navigation.AppNavGraph
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.presentation.MainDispatcherRule
import com.softartdev.notedelight.usecase.crypt.CheckPasswordUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class SignInViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockCheckPasswordUseCase = Mockito.mock(CheckPasswordUseCase::class.java)
    private val mockRouter = Mockito.mock(Router::class.java)
    
    private lateinit var signInViewModel: SignInViewModel

    @Before
    fun setUp() {
        signInViewModel = SignInViewModel(mockCheckPasswordUseCase, mockRouter)
    }

    @Test
    fun showSignInForm() = runTest {
        signInViewModel.stateFlow.test {
            assertEquals(SignInResult.ShowSignInForm, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun navMain() = runTest {
        signInViewModel.stateFlow.test {
            assertEquals(SignInResult.ShowSignInForm, awaitItem())

            val pass = StubEditable("pass")
            Mockito.`when`(mockCheckPasswordUseCase(pass)).thenReturn(true)
            signInViewModel.signIn(pass)
            Mockito.verify(mockRouter).navigateClearingBackStack(route = AppNavGraph.Main)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun showEmptyPassError() = runTest {
        signInViewModel.stateFlow.test {
            assertEquals(SignInResult.ShowSignInForm, awaitItem())

            signInViewModel.signIn(pass = StubEditable(""))
            assertEquals(SignInResult.ShowEmptyPassError, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun showIncorrectPassError() = runTest {
        signInViewModel.stateFlow.test {
            assertEquals(SignInResult.ShowSignInForm, awaitItem())

            val pass = StubEditable("pass")
            Mockito.`when`(mockCheckPasswordUseCase(pass)).thenReturn(false)
            signInViewModel.signIn(pass)
            assertEquals(SignInResult.ShowIncorrectPassError, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun showError() = runTest {
        signInViewModel.stateFlow.test {
            assertEquals(SignInResult.ShowSignInForm, awaitItem())

            val throwable = Throwable()
            Mockito.`when`(mockCheckPasswordUseCase(anyObject())).thenThrow(throwable)
            signInViewModel.signIn(StubEditable("pass"))
            Mockito.verify(mockRouter).navigate(
                route = AppNavGraph.ErrorDialog(message = throwable.message)
            )
            cancelAndIgnoreRemainingEvents()
        }
    }
}