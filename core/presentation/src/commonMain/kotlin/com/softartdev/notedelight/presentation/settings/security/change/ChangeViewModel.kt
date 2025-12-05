package com.softartdev.notedelight.presentation.settings.security.change

import androidx.compose.ui.autofill.AutofillManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.interactor.SnackbarInteractor
import com.softartdev.notedelight.interactor.SnackbarMessage
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.presentation.settings.security.FieldLabel
import com.softartdev.notedelight.usecase.crypt.ChangePasswordUseCase
import com.softartdev.notedelight.usecase.crypt.CheckPasswordUseCase
import com.softartdev.notedelight.util.CoroutineDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChangeViewModel(
    private val checkPasswordUseCase: CheckPasswordUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val snackbarInteractor: SnackbarInteractor,
    private val router: Router,
    private val coroutineDispatchers: CoroutineDispatchers,
) : ViewModel() {
    private val logger = Logger.withTag(this@ChangeViewModel::class.simpleName.toString())
    private val mutableStateFlow: MutableStateFlow<ChangeResult> = MutableStateFlow(ChangeResult())
    val stateFlow: StateFlow<ChangeResult> = mutableStateFlow
    var autofillManager: AutofillManager? = null

    fun onAction(action: ChangeAction) = when (action) {
        is ChangeAction.Cancel -> cancel()
        is ChangeAction.OnEditOldPassword -> onEditOldPassword(action.password)
        is ChangeAction.OnEditNewPassword -> onEditNewPassword(action.password)
        is ChangeAction.OnEditRepeatPassword -> onEditRepeatPassword(action.password)
        is ChangeAction.OnChangeClick -> change()
    }

    private fun onEditOldPassword(password: String) = viewModelScope.launch {
        mutableStateFlow.update(ChangeResult::hideErrors)
        mutableStateFlow.update { it.copy(oldPassword = password) }
    }

    private fun onEditNewPassword(password: String) = viewModelScope.launch {
        mutableStateFlow.update(ChangeResult::hideErrors)
        mutableStateFlow.update { it.copy(newPassword = password) }
    }

    private fun onEditRepeatPassword(password: String) = viewModelScope.launch {
        mutableStateFlow.update(ChangeResult::hideErrors)
        mutableStateFlow.update { it.copy(repeatNewPassword = password) }
    }

    private fun change() = viewModelScope.launch(context = coroutineDispatchers.io) {
        mutableStateFlow.update(ChangeResult::showLoading)
        try {
            val oldPassword = mutableStateFlow.value.oldPassword
            val newPassword = mutableStateFlow.value.newPassword
            val repeatNewPassword = mutableStateFlow.value.repeatNewPassword

            when {
                oldPassword.isEmpty() -> mutableStateFlow.update {
                    it.copy(oldPasswordFieldLabel = FieldLabel.EMPTY_PASSWORD, isOldPasswordError = true)
                }
                newPassword.isEmpty() -> mutableStateFlow.update {
                    it.copy(newPasswordFieldLabel = FieldLabel.EMPTY_PASSWORD, isNewPasswordError = true)
                }
                newPassword != repeatNewPassword -> mutableStateFlow.update {
                    it.copy(
                        repeatPasswordFieldLabel = FieldLabel.PASSWORDS_NOT_MATCH,
                        isRepeatPasswordError = true
                    )
                }
                checkPasswordUseCase(oldPassword) -> {
                    changePasswordUseCase(oldPassword, newPassword)
                    autofillManager?.commit()
                    withContext(coroutineDispatchers.main) {
                        router.popBackStack()
                    }
                }
                else -> mutableStateFlow.update {
                    it.copy(oldPasswordFieldLabel = FieldLabel.INCORRECT_PASSWORD, isOldPasswordError = true)
                }
            }
        } catch (e: Throwable) {
            logger.e(e) { "Error changing password" }
            autofillManager?.cancel()
            e.message?.let { snackbarInteractor.showMessage(SnackbarMessage.Simple(it)) }
        } finally {
            mutableStateFlow.update(ChangeResult::hideLoading)
        }
    }

    private fun cancel() = viewModelScope.launch {
        router.popBackStack()
    }
}