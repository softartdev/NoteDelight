package com.softartdev.notedelight.presentation.settings.security.change

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
import kotlinx.coroutines.withContext

class ChangeViewModel(
    private val checkPasswordUseCase: CheckPasswordUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val biometricInteractor: BiometricInteractor,
    private val snackbarInteractor: SnackbarInteractor,
    private val router: Router,
    private val coroutineDispatchers: CoroutineDispatchers,
    private val autofillInteractor: AutofillInteractor,
) : ViewModel() {
    private val logger = Logger.withTag(this@ChangeViewModel::class.simpleName.toString())

    val stateFlow: StateFlow<ChangeResult>
        field = MutableStateFlow(ChangeResult())

    fun onAction(action: ChangeAction) = when (action) {
        is ChangeAction.Cancel -> cancel()
        is ChangeAction.OnEditOldPassword -> onEditOldPassword(action.password)
        is ChangeAction.OnEditNewPassword -> onEditNewPassword(action.password)
        is ChangeAction.OnEditRepeatPassword -> onEditRepeatPassword(action.password)
        is ChangeAction.OnChangeClick -> change()
    }

    fun attachAutofillManager(autofillManager: Any) = autofillInteractor.attach(autofillManager)

    fun detachAutofillManager() = autofillInteractor.detach()

    private fun onEditOldPassword(password: String) = viewModelScope.launch {
        stateFlow.update(ChangeResult::hideErrors)
        stateFlow.update { it.copy(oldPassword = password) }
    }

    private fun onEditNewPassword(password: String) = viewModelScope.launch {
        stateFlow.update(ChangeResult::hideErrors)
        stateFlow.update { it.copy(newPassword = password) }
    }

    private fun onEditRepeatPassword(password: String) = viewModelScope.launch {
        stateFlow.update(ChangeResult::hideErrors)
        stateFlow.update { it.copy(repeatNewPassword = password) }
    }

    private fun change() = viewModelScope.launch(context = coroutineDispatchers.io) {
        CountingIdlingRes.increment()
        stateFlow.update(ChangeResult::showLoading)
        try {
            val oldPassword = stateFlow.value.oldPassword
            val newPassword = stateFlow.value.newPassword
            val repeatNewPassword = stateFlow.value.repeatNewPassword
            when {
                oldPassword.isEmpty() -> stateFlow.update {
                    it.copy(oldPasswordFieldLabel = FieldLabel.EMPTY_PASSWORD, isOldPasswordError = true)
                }
                newPassword.isEmpty() -> stateFlow.update {
                    it.copy(newPasswordFieldLabel = FieldLabel.EMPTY_PASSWORD, isNewPasswordError = true)
                }
                newPassword != repeatNewPassword -> stateFlow.update {
                    it.copy(
                        repeatPasswordFieldLabel = FieldLabel.PASSWORDS_NOT_MATCH,
                        isRepeatPasswordError = true
                    )
                }
                checkPasswordUseCase(oldPassword) -> {
                    changePasswordUseCase(oldPassword, newPassword)
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
                else -> stateFlow.update {
                    it.copy(oldPasswordFieldLabel = FieldLabel.INCORRECT_PASSWORD, isOldPasswordError = true)
                }
            }
        } catch (e: Throwable) {
            logger.e(e) { "Error changing password" }
            autofillInteractor.cancel()
            e.message?.let { snackbarInteractor.showMessage(SnackbarMessage.Simple(it)) }
        } finally {
            stateFlow.update(ChangeResult::hideLoading)
            CountingIdlingRes.decrement()
        }
    }

    private fun cancel() = viewModelScope.launch {
        router.popBackStack()
    }
}
