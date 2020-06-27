package com.softartdev.notedelight.ui.signin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.shared.test.util.MainCoroutineRule
import com.softartdev.notedelight.shared.test.util.StubEditable
import com.softartdev.notedelight.shared.test.util.anyObject
import com.softartdev.notedelight.shared.test.util.assertValues
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
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
    private val signInViewModel = SignInViewModel(cryptUseCase)

    @Test
    fun navMain() = mainCoroutineRule.runBlockingTest {
        val pass = StubEditable("pass")
        Mockito.`when`(cryptUseCase.checkPassword(pass)).thenReturn(true)
        signInViewModel.resultLiveData.assertValues(
                SignInResult.ShowProgress,
                SignInResult.NavMain
        ) {
            signInViewModel.signIn(pass)
        }
    }

    @Test
    fun showEmptyPassError() = signInViewModel.resultLiveData.assertValues(
            SignInResult.ShowProgress,
            SignInResult.ShowEmptyPassError
    ) {
        signInViewModel.signIn(pass = StubEditable(""))
    }

    @Test
    fun showIncorrectPassError() = mainCoroutineRule.runBlockingTest {
        val pass = StubEditable("pass")
        Mockito.`when`(cryptUseCase.checkPassword(pass)).thenReturn(false)
        signInViewModel.resultLiveData.assertValues(
                SignInResult.ShowProgress,
                SignInResult.ShowIncorrectPassError
        ) {
            signInViewModel.signIn(pass)
        }
    }

    @Test
    fun showError() = mainCoroutineRule.runBlockingTest {
        val throwable = Throwable()
        Mockito.`when`(cryptUseCase.checkPassword(anyObject())).then { throw throwable }
        signInViewModel.resultLiveData.assertValues(
                SignInResult.ShowProgress,
                SignInResult.ShowError(throwable)
        ) {
            signInViewModel.signIn(StubEditable("pass"))
        }
    }
}