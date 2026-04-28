package com.softartdev.notedelight.presentation.signin

import androidx.compose.ui.autofill.AutofillManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.interactor.BiometricInteractor
import com.softartdev.notedelight.interactor.DecryptedPasswordResult
import com.softartdev.notedelight.interactor.BiometricResult
import com.softartdev.notedelight.navigation.AppNavGraph
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.usecase.crypt.CheckPasswordUseCase
import com.softartdev.notedelight.util.CountingIdlingRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private val checkPasswordUseCase: CheckPasswordUseCase,
    private val biometricInteractor: BiometricInteractor,
    private val router: Router
) : ViewModel() {
    private val logger = Logger.withTag(this@SignInViewModel::class.simpleName.toString())
    private val mutableStateFlow: MutableStateFlow<SignInResult> = MutableStateFlow(
        value = SignInResult.ShowSignInForm
    )
    val stateFlow: StateFlow<SignInResult> = mutableStateFlow

    private val mutableBiometricVisibleFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val biometricVisibleFlow: StateFlow<Boolean> = mutableBiometricVisibleFlow

    var autofillManager: AutofillManager? = null

    fun onAction(action: SignInAction) = when (action) {
        is SignInAction.OnSettingsClick -> router.navigateClearingBackStack(AppNavGraph.Settings)
        is SignInAction.OnSignInClick -> signIn(action.pass)
        is SignInAction.RefreshBiometric -> refreshBiometric()
        is SignInAction.OnBiometricClick -> signInWithBiometric(
            title = action.title,
            subtitle = action.subtitle,
            negativeButton = action.negativeButton
        )
    }

    private fun refreshBiometric() = viewModelScope.launch {
        mutableBiometricVisibleFlow.value = biometricInteractor.hasStoredPassword() && biometricInteractor.canAuthenticate()
    }

    private fun signInWithBiometric(title: String, subtitle: String, negativeButton: String) = viewModelScope.launch {
        CountingIdlingRes.increment()
        mutableStateFlow.value = SignInResult.ShowProgress
        try {
            when (val res: DecryptedPasswordResult = biometricInteractor.decryptStoredPassword(title, subtitle, negativeButton)) {
                is DecryptedPasswordResult.Success -> signInInternal(res.password)
                is DecryptedPasswordResult.Failure -> when (res.result) {
                    BiometricResult.Cancelled -> mutableStateFlow.value = SignInResult.ShowSignInForm
                    BiometricResult.Unavailable -> {
                        biometricInteractor.clearStoredPassword()
                        mutableBiometricVisibleFlow.value = false
                        mutableStateFlow.value = SignInResult.ShowSignInForm
                    }
                    else -> mutableStateFlow.value = SignInResult.ShowBiometricError
                }
            }
        } catch (error: Throwable) {
            logger.e(error) { "Error during biometric sign in" }
            router.navigate(route = AppNavGraph.ErrorDialog(message = error.message))
            mutableStateFlow.value = SignInResult.ShowSignInForm
        } finally {
            CountingIdlingRes.decrement()
        }
    }

    private fun signIn(pass: CharSequence) = viewModelScope.launch {
        CountingIdlingRes.increment()
        mutableStateFlow.value = SignInResult.ShowProgress
        try {
            mutableStateFlow.value = signInInternal(pass)
        } catch (error: Throwable) {
            logger.e(error) { "Error during sign in" }
            autofillManager?.cancel()
            router.navigate(route = AppNavGraph.ErrorDialog(message = error.message))
            mutableStateFlow.value = SignInResult.ShowSignInForm
        } finally {
            CountingIdlingRes.decrement()
        }
    }

    private suspend fun signInInternal(pass: CharSequence): SignInResult = when {
        pass.isEmpty() -> SignInResult.ShowEmptyPassError
        checkPasswordUseCase(pass) -> {
            autofillManager?.commit()
            router.navigateClearingBackStack(AppNavGraph.Main)
            SignInResult.ShowSignInForm
        }
        else -> SignInResult.ShowIncorrectPassError
    }
}
