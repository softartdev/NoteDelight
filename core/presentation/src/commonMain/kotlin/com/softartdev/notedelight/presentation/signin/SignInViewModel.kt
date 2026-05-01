package com.softartdev.notedelight.presentation.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.interactor.AutofillInteractor
import com.softartdev.notedelight.interactor.BiometricInteractor
import com.softartdev.notedelight.interactor.BiometricPlatformWrapper
import com.softartdev.notedelight.interactor.DecryptedPasswordResult
import com.softartdev.notedelight.interactor.SnackbarInteractor
import com.softartdev.notedelight.interactor.SnackbarMessage
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
    private val snackbarInteractor: SnackbarInteractor,
    private val autofillInteractor: AutofillInteractor,
) : ViewModel() {
    private val logger = Logger.withTag(this@SignInViewModel::class.simpleName.toString())

    private val mutableStateFlow: MutableStateFlow<SignInResult> = MutableStateFlow(SignInResult())
    val stateFlow: StateFlow<SignInResult> = mutableStateFlow

    fun onAction(action: SignInAction) = when (action) {
        is SignInAction.OnSettingsClick -> router.navigateClearingBackStack(AppNavGraph.Settings)
        is SignInAction.OnSignInClick -> signIn(action.pass)
        is SignInAction.RefreshBiometric -> refreshBiometric()
        is SignInAction.OnBiometricClick -> signInWithBiometric(
            title = action.title,
            subtitle = action.subtitle,
            negativeButton = action.negativeButton,
            biometricPlatformWrapper = action.biometricPlatformWrapper,
        )
    }

    fun attachAutofillManager(autofillManager: Any) = autofillInteractor.attach(autofillManager)

    fun detachAutofillManager() = autofillInteractor.detach()

    private fun refreshBiometric() = viewModelScope.launch {
        val visible: Boolean = biometricInteractor.hasStoredPassword() && biometricInteractor.canAuthenticate()
        mutableStateFlow.update { it.copy(biometricVisible = visible) }
    }

    private fun signInWithBiometric(
        title: String,
        subtitle: String,
        negativeButton: String,
        biometricPlatformWrapper: BiometricPlatformWrapper,
    ) = viewModelScope.launch {
        CountingIdlingRes.increment()
        mutableStateFlow.update(SignInResult::hideErrors)
        mutableStateFlow.update(SignInResult::showLoading)
        try {
            when (val res: DecryptedPasswordResult = biometricInteractor.decryptStoredPassword(
                title = title,
                subtitle = subtitle,
                negativeButton = negativeButton,
                biometricPlatformWrapper = biometricPlatformWrapper,
            )) {
                is DecryptedPasswordResult.Success -> signInInternal(pass = res.password)
                is DecryptedPasswordResult.Cancelled -> Unit
                is DecryptedPasswordResult.Unavailable -> {
                    biometricInteractor.clearStoredPassword()
                    mutableStateFlow.update(SignInResult::hideBiometric)
                }
                is DecryptedPasswordResult.Failure -> {
                    logger.e { res.message }
                    snackbarInteractor.showMessage(SnackbarMessage.Simple(res.message))
                }
            }
        } catch (error: Throwable) {
            logger.e(error) { "Error during biometric sign in" }
            router.navigate(route = AppNavGraph.ErrorDialog(message = error.message))
        } finally {
            mutableStateFlow.update(SignInResult::hideLoading)
            CountingIdlingRes.decrement()
        }
    }

    private fun signIn(pass: CharSequence) = viewModelScope.launch {
        CountingIdlingRes.increment()
        mutableStateFlow.update(SignInResult::hideErrors)
        mutableStateFlow.update(SignInResult::showLoading)
        try {
            signInInternal(pass)
        } catch (error: Throwable) {
            logger.e(error) { "Error during sign in" }
            autofillInteractor.cancel()
            router.navigate(route = AppNavGraph.ErrorDialog(message = error.message))
        } finally {
            mutableStateFlow.update(SignInResult::hideLoading)
            CountingIdlingRes.decrement()
        }
    }

    private suspend fun signInInternal(pass: CharSequence) = when {
        pass.isEmpty() -> mutableStateFlow.update(SignInResult::showEmptyPasswordError)
        checkPasswordUseCase(pass) -> {
            autofillInteractor.commit()
            router.navigateClearingBackStack(AppNavGraph.Main)
        }
        else -> mutableStateFlow.update(SignInResult::showIncorrectPasswordError)
    }
}
