package com.softartdev.notedelight.presentation.signin

import androidx.compose.ui.autofill.AutofillManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.navigation.AppNavGraph
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.usecase.crypt.CheckPasswordUseCase
import com.softartdev.notedelight.util.CountingIdlingRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private val checkPasswordUseCase: CheckPasswordUseCase,
    private val router: Router,
    private val biometricAuthenticator: BiometricAuthenticator = NoOpBiometricAuthenticator,
) : ViewModel() {
    private val logger = Logger.withTag(this@SignInViewModel::class.simpleName.toString())
    private val mutableStateFlow: MutableStateFlow<SignInResult> = MutableStateFlow(
        value = SignInResult.ShowSignInForm
    )
    val stateFlow: StateFlow<SignInResult> = mutableStateFlow
    var autofillManager: AutofillManager? = null
    val isBiometricAvailable: Boolean
        get() = biometricAuthenticator.isAvailable()

    fun onAction(action: SignInAction) = when (action) {
        is SignInAction.OnSettingsClick -> router.navigateClearingBackStack(AppNavGraph.Settings)
        is SignInAction.OnSignInClick -> signIn(action.pass)
        is SignInAction.OnBiometricSignInClick -> signInWithBiometric()
    }

    private fun signIn(pass: CharSequence) = viewModelScope.launch {
        CountingIdlingRes.increment()
        mutableStateFlow.value = SignInResult.ShowProgress
        try {
            mutableStateFlow.value = when {
                pass.isEmpty() -> SignInResult.ShowEmptyPassError
                checkPasswordUseCase(pass) -> {
                    autofillManager?.commit()
                    router.navigateClearingBackStack(AppNavGraph.Main)
                    SignInResult.ShowSignInForm
                }
                else -> SignInResult.ShowIncorrectPassError
            }
        } catch (error: Throwable) {
            logger.e(error) { "Error during sign in" }
            autofillManager?.cancel()
            router.navigate(route = AppNavGraph.ErrorDialog(message = error.message))
            mutableStateFlow.value = SignInResult.ShowSignInForm
        } finally {
            CountingIdlingRes.decrement()
        }
    }

    private fun signInWithBiometric() = viewModelScope.launch {
        if (!biometricAuthenticator.isAvailable()) return@launch
        CountingIdlingRes.increment()
        mutableStateFlow.value = SignInResult.ShowProgress
        try {
            when (val result = biometricAuthenticator.authenticate()) {
                BiometricAuthResult.Success -> {
                    autofillManager?.commit()
                    router.navigateClearingBackStack(AppNavGraph.Main)
                }
                BiometricAuthResult.Cancelled -> Unit
                is BiometricAuthResult.Error -> {
                    logger.e(result.throwable) { "Error during biometric sign in" }
                    autofillManager?.cancel()
                    router.navigate(route = AppNavGraph.ErrorDialog(message = result.throwable.message))
                }
            }
        } finally {
            mutableStateFlow.value = SignInResult.ShowSignInForm
            CountingIdlingRes.decrement()
        }
    }
}
