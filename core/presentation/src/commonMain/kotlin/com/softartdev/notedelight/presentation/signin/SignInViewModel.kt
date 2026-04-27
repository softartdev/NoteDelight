package com.softartdev.notedelight.presentation.signin

import androidx.compose.ui.autofill.AutofillManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.interactor.BiometricAuthResult
import com.softartdev.notedelight.interactor.BiometricAuthService
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
    private val biometricAuthService: BiometricAuthService
) : ViewModel() {
    private val logger = Logger.withTag(this@SignInViewModel::class.simpleName.toString())
    private val mutableStateFlow: MutableStateFlow<SignInResult> = MutableStateFlow(
        value = SignInResult.ShowSignInForm
    )
    val stateFlow: StateFlow<SignInResult> = mutableStateFlow
    var autofillManager: AutofillManager? = null

    init {
        checkBiometricAvailability()
    }

    fun onAction(action: SignInAction) = when (action) {
        is SignInAction.OnSettingsClick -> router.navigateClearingBackStack(AppNavGraph.Settings)
        is SignInAction.OnSignInClick -> signIn(action.pass)
        is SignInAction.OnBiometricClick -> signInWithBiometric()
    }

    private fun checkBiometricAvailability() = viewModelScope.launch {
        if (biometricAuthService.isBiometricAvailable()) {
            mutableStateFlow.value = SignInResult.ShowBiometricAvailable
        }
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
        CountingIdlingRes.increment()
        mutableStateFlow.value = SignInResult.ShowBiometricInProgress
        try {
            when (biometricAuthService.authenticate()) {
                BiometricAuthResult.Success -> {
                    router.navigateClearingBackStack(AppNavGraph.Main)
                    mutableStateFlow.value = SignInResult.ShowBiometricSuccess
                }
                BiometricAuthResult.Failed -> mutableStateFlow.value = SignInResult.ShowBiometricFailed
                BiometricAuthResult.FallbackToPassword ->
                    mutableStateFlow.value = SignInResult.ShowBiometricFallbackToPassword
            }
        } catch (error: Throwable) {
            logger.e(error) { "Error during biometric sign in" }
            mutableStateFlow.value = SignInResult.ShowBiometricFallbackToPassword
        } finally {
            CountingIdlingRes.decrement()
        }
    }
}
