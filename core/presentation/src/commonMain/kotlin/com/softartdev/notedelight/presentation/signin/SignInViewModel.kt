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

    var autofillManager: AutofillManager? = null

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
        mutableStateFlow.update { it.copy(state = SignInResult.State.Progress) }
        try {
            when (val res: DecryptedPasswordResult = biometricInteractor.decryptStoredPassword(
                title = title,
                subtitle = subtitle,
                negativeButton = negativeButton,
                biometricPlatformWrapper = biometricPlatformWrapper,
            )) {
                is DecryptedPasswordResult.Success -> mutableStateFlow.update {
                    it.copy(state = signInInternal(res.password))
                }
                is DecryptedPasswordResult.Cancelled -> mutableStateFlow.update {
                    it.copy(state = SignInResult.State.Form)
                }
                is DecryptedPasswordResult.Unavailable -> {
                    biometricInteractor.clearStoredPassword()
                    mutableStateFlow.update {
                        it.copy(state = SignInResult.State.Form, biometricVisible = false)
                    }
                }
                is DecryptedPasswordResult.Failure -> {
                    logger.e { res.message }
                    snackbarInteractor.showMessage(SnackbarMessage.Simple(res.message))
                    mutableStateFlow.update { it.copy(state = SignInResult.State.Form) }
                }
            }
        } catch (error: Throwable) {
            logger.e(error) { "Error during biometric sign in" }
            router.navigate(route = AppNavGraph.ErrorDialog(message = error.message))
            mutableStateFlow.update { it.copy(state = SignInResult.State.Form) }
        } finally {
            CountingIdlingRes.decrement()
        }
    }

    private fun signIn(pass: CharSequence) = viewModelScope.launch {
        CountingIdlingRes.increment()
        mutableStateFlow.update { it.copy(state = SignInResult.State.Progress) }
        try {
            val nextState: SignInResult.State = signInInternal(pass)
            mutableStateFlow.update { it.copy(state = nextState) }
        } catch (error: Throwable) {
            logger.e(error) { "Error during sign in" }
            autofillManager?.cancel()
            router.navigate(route = AppNavGraph.ErrorDialog(message = error.message))
            mutableStateFlow.update { it.copy(state = SignInResult.State.Form) }
        } finally {
            CountingIdlingRes.decrement()
        }
    }

    private suspend fun signInInternal(pass: CharSequence): SignInResult.State = when {
        pass.isEmpty() -> SignInResult.State.Error.EmptyPass
        checkPasswordUseCase(pass) -> {
            autofillManager?.commit()
            router.navigateClearingBackStack(AppNavGraph.Main)
            SignInResult.State.Form
        }
        else -> SignInResult.State.Error.IncorrectPass
    }
}
