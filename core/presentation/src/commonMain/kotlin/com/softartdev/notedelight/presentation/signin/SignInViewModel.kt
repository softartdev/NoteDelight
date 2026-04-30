package com.softartdev.notedelight.presentation.signin

import androidx.compose.ui.autofill.AutofillManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
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
) : ViewModel() {
    private val logger = Logger.withTag(this@SignInViewModel::class.simpleName.toString())

    private val mutableStateFlow: MutableStateFlow<SignInResult> = MutableStateFlow(SignInResult())
    val stateFlow: StateFlow<SignInResult> = mutableStateFlow

    var autofillManager: AutofillManager? = null //TODO wrap in interactor for get rid of androidx.compose deps in presentation-modules

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
        mutableStateFlow.update { it.copy(loading = true, errorType = null) }
        try {
            when (val res: DecryptedPasswordResult = biometricInteractor.decryptStoredPassword(
                title = title,
                subtitle = subtitle,
                negativeButton = negativeButton,
                biometricPlatformWrapper = biometricPlatformWrapper,
            )) {
                is DecryptedPasswordResult.Success -> mutableStateFlow.update {
                    it.updateFromSignInInternal(signInInternal(res.password))
                }
                is DecryptedPasswordResult.Cancelled -> mutableStateFlow.update {
                    it.copy(loading = false, errorType = null)
                }
                is DecryptedPasswordResult.Unavailable -> {
                    biometricInteractor.clearStoredPassword()
                    mutableStateFlow.update {
                        it.copy(loading = false, errorType = null, biometricVisible = false)
                    }
                }
                is DecryptedPasswordResult.Failure -> {
                    logger.e { res.message }
                    snackbarInteractor.showMessage(SnackbarMessage.Simple(res.message))
                    mutableStateFlow.update { it.copy(loading = false, errorType = null) }
                }
            }
        } catch (error: Throwable) {
            logger.e(error) { "Error during biometric sign in" }
            router.navigate(route = AppNavGraph.ErrorDialog(message = error.message))
            mutableStateFlow.update { it.copy(loading = false, errorType = null) }
        } finally {
            CountingIdlingRes.decrement()
        }
    }

    private fun signIn(pass: CharSequence) = viewModelScope.launch {
        CountingIdlingRes.increment()
        mutableStateFlow.update { it.copy(loading = true, errorType = null) }
        try {
            val nextState: SignInInternalResult = signInInternal(pass)
            mutableStateFlow.update { it.updateFromSignInInternal(nextState) }
        } catch (error: Throwable) {
            logger.e(error) { "Error during sign in" }
            autofillManager?.cancel()
            router.navigate(route = AppNavGraph.ErrorDialog(message = error.message))
            mutableStateFlow.update { it.copy(loading = false, errorType = null) }
        } finally {
            CountingIdlingRes.decrement()
        }
    }

    private suspend fun signInInternal(pass: CharSequence): SignInInternalResult = when {
        pass.isEmpty() -> SignInInternalResult.Error(ErrorType.EMPTY_PASSWORD)
        checkPasswordUseCase(pass) -> {
            autofillManager?.commit()
            router.navigateClearingBackStack(AppNavGraph.Main)
            SignInInternalResult.Success
        }
        else -> SignInInternalResult.Error(ErrorType.INCORRECT_PASSWORD)
    }
}

private sealed interface SignInInternalResult {
    data object Success : SignInInternalResult
    data class Error(val type: ErrorType) : SignInInternalResult
}

private fun SignInResult.updateFromSignInInternal(result: SignInInternalResult): SignInResult = when (result) {
    SignInInternalResult.Success -> copy(loading = false, errorType = null)
    is SignInInternalResult.Error -> copy(loading = false, errorType = result.type)
}

