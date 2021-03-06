package com.softartdev.notedelight.ui.signin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.shared.test.util.MainCoroutineRule
import com.softartdev.notedelight.shared.test.util.StubEditable
import com.softartdev.notedelight.shared.test.util.anyObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class SignInViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val cryptUseCase = Mockito.mock(CryptUseCase::class.java)
    private lateinit var signInViewModel: SignInViewModel

    @Before
    fun setUp() {
        signInViewModel = SignInViewModel(cryptUseCase)
    }

    @Test
    fun showSignInForm() = runBlocking {
        signInViewModel.resultStateFlow.test {
            assertEquals(SignInResult.ShowSignInForm, expectItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun navMain() = runBlocking {
        signInViewModel.resultStateFlow.test {
            assertEquals(SignInResult.ShowSignInForm, expectItem())

            val pass = StubEditable("pass")
            Mockito.`when`(cryptUseCase.checkPassword(pass)).thenReturn(true)
            signInViewModel.signIn(pass)
            assertEquals(SignInResult.ShowProgress, expectItem())
            assertEquals(SignInResult.NavMain, expectItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun showEmptyPassError() = runBlocking {
        signInViewModel.resultStateFlow.test {
            assertEquals(SignInResult.ShowSignInForm, expectItem())

            signInViewModel.signIn(pass = StubEditable(""))
            assertEquals(SignInResult.ShowProgress, expectItem())
            assertEquals(SignInResult.ShowEmptyPassError, expectItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun showIncorrectPassError() = runBlocking {
        signInViewModel.resultStateFlow.test {
            assertEquals(SignInResult.ShowSignInForm, expectItem())

            val pass = StubEditable("pass")
            Mockito.`when`(cryptUseCase.checkPassword(pass)).thenReturn(false)
            signInViewModel.signIn(pass)
            assertEquals(SignInResult.ShowProgress, expectItem())
            assertEquals(SignInResult.ShowIncorrectPassError, expectItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun showError() = runBlocking {
        signInViewModel.resultStateFlow.test {
            assertEquals(SignInResult.ShowSignInForm, expectItem())

            val throwable = Throwable()
            Mockito.`when`(cryptUseCase.checkPassword(anyObject())).then { throw throwable }
            signInViewModel.signIn(StubEditable("pass"))
            assertEquals(SignInResult.ShowProgress, expectItem())
            assertEquals(SignInResult.ShowError(throwable), expectItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}