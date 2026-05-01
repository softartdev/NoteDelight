package com.softartdev.notedelight.presentation.settings.security.enter

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
import com.softartdev.notedelight.usecase.crypt.CheckPasswordUseCase
import com.softartdev.notedelight.util.CoroutineDispatchers
import com.softartdev.notedelight.util.CountingIdlingRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EnterViewModel(
    private val checkPasswordUseCase: CheckPasswordUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val biometricInteractor: BiometricInteractor,
    private val snackbarInteractor: SnackbarInteractor,
    private val router: Router,
    private val coroutineDispatchers: CoroutineDispatchers,
    private val autofillInteractor: AutofillInteractor,
) : ViewModel() {
    private val logger = Logger.withTag(this@EnterViewModel::class.simpleName.toString())
    private val mutableStateFlow: MutableStateFlow<EnterResult> = MutableStateFlow(EnterResult())
    val stateFlow: StateFlow<EnterResult> = mutableStateFlow

    fun onAction(action: EnterAction) = when (action) {
        is EnterAction.Cancel -> cancel()
        is EnterAction.OnEditPassword -> onEditPassword(action.password)
        is EnterAction.TogglePasswordVisibility -> togglePasswordVisibility()
        is EnterAction.OnEnterClick -> enterCheck()
    }

    fun attachAutofillManager(autofillManager: Any) = autofillInteractor.attach(autofillManager)

    fun detachAutofillManager() = autofillInteractor.detach()

    private fun onEditPassword(password: String) = viewModelScope.launch {
        mutableStateFlow.update(EnterResult::hideError)
        mutableStateFlow.update { it.copy(fieldLabel = FieldLabel.ENTER_PASSWORD) }
        mutableStateFlow.update { it.copy(password = password) }
    }

    private fun togglePasswordVisibility() = viewModelScope.launch {
        mutableStateFlow.update(EnterResult::togglePasswordVisibility)
    }

    private fun enterCheck() = viewModelScope.launch(context = coroutineDispatchers.io) {
        CountingIdlingRes.increment()
        mutableStateFlow.update(EnterResult::showLoading)
        try {
            val password = mutableStateFlow.value.password
            when {
                password.isEmpty() -> {
                    mutableStateFlow.update { it.copy(fieldLabel = FieldLabel.EMPTY_PASSWORD) }
                    mutableStateFlow.update(EnterResult::showError)
                }
                checkPasswordUseCase(password) -> {
                    changePasswordUseCase(password, null)
                    if (biometricInteractor.hasStoredPassword()) {
                        biometricInteractor.clearStoredPassword()
                        snackbarInteractor.showMessage(
                            message = SnackbarMessage.Resource(
                                res = SnackbarTextResource.BIOMETRIC_DISABLED_PASSWORD_CHANGED
                            )
                        )
                    }
                    autofillInteractor.commit()
                    navigateUp()
                }
                else -> {
                    mutableStateFlow.update { it.copy(fieldLabel = FieldLabel.INCORRECT_PASSWORD) }
                    mutableStateFlow.update(EnterResult::showError)
                }
            }
        } catch (e: Throwable) {
            logger.e(e) { "Error entering password" }
            autofillInteractor.cancel()
            e.message?.let { snackbarInteractor.showMessage(SnackbarMessage.Simple(it)) }
        } finally {
            mutableStateFlow.update(EnterResult::hideLoading)
            CountingIdlingRes.decrement()
        }
    }

    private fun cancel() = viewModelScope.launch {
        router.popBackStack()
    }

    private fun navigateUp() = viewModelScope.launch {
        router.popBackStack()
    }
}
