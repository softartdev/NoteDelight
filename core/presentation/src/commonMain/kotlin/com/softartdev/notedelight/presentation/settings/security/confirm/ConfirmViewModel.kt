package com.softartdev.notedelight.presentation.settings.security.confirm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.interactor.AutofillInteractor
import com.softartdev.notedelight.interactor.BiometricInteractor
import com.softartdev.notedelight.interactor.SnackbarInteractor
import com.softartdev.notedelight.interactor.SnackbarMessage
import com.softartdev.notedelight.interactor.SnackbarTextResource
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.presentation.settings.security.FieldLabel
import com.softartdev.notedelight.usecase.crypt.ChangePasswordUseCase
import com.softartdev.notedelight.util.CoroutineDispatchers
import com.softartdev.notedelight.util.CountingIdlingRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfirmViewModel(
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val biometricInteractor: BiometricInteractor,
    private val snackbarInteractor: SnackbarInteractor,
    private val router: Router,
    private val coroutineDispatchers: CoroutineDispatchers,
    private val autofillInteractor: AutofillInteractor,
) : ViewModel() {
    private val logger = Logger.withTag(this@ConfirmViewModel::class.simpleName.toString())

    val stateFlow: StateFlow<ConfirmResult>
        field = MutableStateFlow(value = ConfirmResult())

    fun onAction(action: ConfirmAction) = when (action) {
        is ConfirmAction.Cancel -> cancel()
        is ConfirmAction.OnEditPassword -> onEditPassword(action.password)
        is ConfirmAction.OnEditRepeatPassword -> onEditRepeatPassword(action.password)
        is ConfirmAction.OnConfirmClick -> confirm()
    }

    fun attachAutofillManager(autofillManager: Any) = autofillInteractor.attach(autofillManager)

    fun detachAutofillManager() = autofillInteractor.detach()

    private fun onEditPassword(password: String) = viewModelScope.launch {
        stateFlow.update(ConfirmResult::hideErrors)
        stateFlow.update { it.copy(password = password) }
    }

    private fun onEditRepeatPassword(password: String) = viewModelScope.launch {
        stateFlow.update(ConfirmResult::hideErrors)
        stateFlow.update { it.copy(repeatPassword = password) }
    }

    private fun confirm() = viewModelScope.launch(context = coroutineDispatchers.io) {
        CountingIdlingRes.increment()
        stateFlow.update(ConfirmResult::showLoading)
        try {
            val password = stateFlow.value.password
            val repeatPassword = stateFlow.value.repeatPassword
            when {
                password != repeatPassword -> stateFlow.update {
                    it.copy(
                        repeatPasswordFieldLabel = FieldLabel.PASSWORDS_NOT_MATCH,
                        isRepeatPasswordError = true
                    )
                }
                password.isEmpty() -> stateFlow.update {
                    it.copy(
                        passwordFieldLabel = FieldLabel.EMPTY_PASSWORD,
                        isPasswordError = true
                    )
                }
                else -> {
                    changePasswordUseCase(null, password)
                    if (biometricInteractor.hasStoredPassword()) {
                        biometricInteractor.clearStoredPassword()
                        snackbarInteractor.showMessage(
                            message = SnackbarMessage.Resource(
                                res = SnackbarTextResource.BIOMETRIC_DISABLED_PASSWORD_CHANGED
                            )
                        )
                    }
                    autofillInteractor.commit()
                    withContext(coroutineDispatchers.main) {
                        router.popBackStack()
                    }
                }
            }
        } catch (e: Throwable) {
            logger.e(e) { "Error confirming password" }
            autofillInteractor.cancel()
            e.message?.let { snackbarInteractor.showMessage(SnackbarMessage.Simple(it)) }
        } finally {
            stateFlow.update(ConfirmResult::hideLoading)
            CountingIdlingRes.decrement()
        }
    }

    private fun cancel() = viewModelScope.launch {
        router.popBackStack()
    }
}
