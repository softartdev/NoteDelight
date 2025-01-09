package com.softartdev.notedelight.presentation.settings.security.change

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.presentation.settings.security.FieldLabel
import com.softartdev.notedelight.usecase.crypt.ChangePasswordUseCase
import com.softartdev.notedelight.usecase.crypt.CheckPasswordUseCase
import com.softartdev.notedelight.util.CoroutineDispatchers
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChangeViewModel(
    private val checkPasswordUseCase: CheckPasswordUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val router: Router,
    private val coroutineDispatchers: CoroutineDispatchers,
) : ViewModel() {
    private val mutableStateFlow: MutableStateFlow<ChangeResult> = MutableStateFlow(
        value = ChangeResult(
            onCancel = this::cancel,
            onChangeClick = this::change,
            onEditOldPassword = this::onEditOldPassword,
            onEditNewPassword = this::onEditNewPassword,
            onEditRepeatPassword = this::onEditRepeatPassword,
            disposeOneTimeEvents = this::disposeOneTimeEvents
        )
    )
    val stateFlow: StateFlow<ChangeResult> = mutableStateFlow

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
                    it.copy(oldPasswordFieldLabel = FieldLabel.EMPTY, isOldPasswordError = true)
                }
                newPassword.isEmpty() -> mutableStateFlow.update {
                    it.copy(newPasswordFieldLabel = FieldLabel.EMPTY, isNewPasswordError = true)
                }
                newPassword != repeatNewPassword -> mutableStateFlow.update {
                    it.copy(
                        repeatPasswordFieldLabel = FieldLabel.NO_MATCH,
                        isRepeatPasswordError = true
                    )
                }
                checkPasswordUseCase(oldPassword) -> {
                    changePasswordUseCase(oldPassword, newPassword)
                    withContext(coroutineDispatchers.main) {
                        router.popBackStack()
                    }
                }
                else -> mutableStateFlow.update {
                    it.copy(oldPasswordFieldLabel = FieldLabel.INCORRECT, isOldPasswordError = true)
                }
            }
        } catch (e: Throwable) {
            Napier.e("❌", e)
            mutableStateFlow.update { it.copy(snackBarMessageType = e.message) }
        } finally {
            mutableStateFlow.update(ChangeResult::hideLoading)
        }
    }

    private fun cancel() = viewModelScope.launch {
        router.popBackStack()
    }

    private fun disposeOneTimeEvents() = viewModelScope.launch {
        mutableStateFlow.update(ChangeResult::hideSnackBarMessage)
    }
}
