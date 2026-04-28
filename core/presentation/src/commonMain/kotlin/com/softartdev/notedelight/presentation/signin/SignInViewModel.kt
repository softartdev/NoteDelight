package com.softartdev.notedelight.presentation.signin

import androidx.compose.ui.autofill.AutofillManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.interactor.BiometricInteractor
import com.softartdev.notedelight.interactor.BiometricResult
import com.softartdev.notedelight.interactor.DecryptedPasswordResult
import com.softartdev.notedelight.navigation.AppNavGraph
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.usecase.crypt.CheckPasswordUseCase
import com.softartdev.notedelight.util.CountingIdlingRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel(
    private val checkPasswordUseCase: CheckPasswordUseCase,
    private val biometricInteractor: BiometricInteractor,
    private val router: Router,
) : ViewModel() {
    private val logger = Logger.withTag(this@SignInViewModel::class.simpleName.toString())

    private val mutableStateFlow: MutableStateFlow<SignInResult> = MutableStateFlow(SignInResult.Form())
    val stateFlow: StateFlow<SignInResult> = mutableStateFlow

    var autofillManager: AutofillManager? = null

    fun onAction(action: SignInAction) = when (action) {
        is SignInAction.OnSettingsClick -> router.navigateClearingBackStack(AppNavGraph.Settings)
        is SignInAction.OnSignInClick -> signIn(action.pass)
        is SignInAction.RefreshBiometric -> refreshBiometric()
        is SignInAction.OnBiometricClick -> signInWithBiometric(
            title = action.title,
            subtitle = action.subtitle,
            negativeButton = action.negativeButton,
        )
    }

    private fun refreshBiometric() = viewModelScope.launch {
        val visible: Boolean = biometricInteractor.hasStoredPassword() && biometricInteractor.canAuthenticate()
        mutableStateFlow.value = SignInResult.Form(biometricVisible = visible)
    }

    private fun signInWithBiometric(title: String, subtitle: String, negativeButton: String) = viewModelScope.launch {
        CountingIdlingRes.increment()
        setState { SignInResult.Progress(it) }
        try {
            when (val res: DecryptedPasswordResult = biometricInteractor.decryptStoredPassword(title, subtitle, negativeButton)) {
                is DecryptedPasswordResult.Success -> setState { signInInternal(res.password, it) }
                is DecryptedPasswordResult.Failure -> when (res.result) {
                    BiometricResult.Cancelled -> setState { SignInResult.Form(it) }
                    BiometricResult.Unavailable -> {
                        biometricInteractor.clearStoredPassword()
                        mutableStateFlow.value = SignInResult.Form(biometricVisible = false)
                    }
                    else -> setState { SignInResult.Error.Biometric(it) }
                }
            }
        } catch (error: Throwable) {
            logger.e(error) { "Error during biometric sign in" }
            router.navigate(route = AppNavGraph.ErrorDialog(message = error.message))
            setState { SignInResult.Form(it) }
        } finally {
            CountingIdlingRes.decrement()
        }
    }

    private fun signIn(pass: CharSequence) = viewModelScope.launch {
        CountingIdlingRes.increment()
        setState { SignInResult.Progress(it) }
        try {
            setState { signInInternal(pass, it) }
        } catch (error: Throwable) {
            logger.e(error) { "Error during sign in" }
            autofillManager?.cancel()
            router.navigate(route = AppNavGraph.ErrorDialog(message = error.message))
            setState { SignInResult.Form(it) }
        } finally {
            CountingIdlingRes.decrement()
        }
    }

    private suspend fun signInInternal(pass: CharSequence, biometricVisible: Boolean): SignInResult = when {
        pass.isEmpty() -> SignInResult.Error.EmptyPass(biometricVisible)
        checkPasswordUseCase(pass) -> {
            autofillManager?.commit()
            router.navigateClearingBackStack(AppNavGraph.Main)
            SignInResult.Form(biometricVisible)
        }
        else -> SignInResult.Error.IncorrectPass(biometricVisible)
    }

    // Atomically rewrites the SignInResult while preserving the current biometricVisible flag.
    private inline fun setState(transform: (biometricVisible: Boolean) -> SignInResult) {
        mutableStateFlow.update { transform(it.biometricVisible) }
    }
}
