package com.softartdev.notedelight.presentation.settings.security.confirm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.presentation.settings.security.FieldLabel
import com.softartdev.notedelight.usecase.crypt.ChangePasswordUseCase
import com.softartdev.notedelight.util.CoroutineDispatchers
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfirmViewModel(
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val router: Router,
    private val coroutineDispatchers: CoroutineDispatchers,
) : ViewModel() {
    private val mutableStateFlow: MutableStateFlow<ConfirmResult> = MutableStateFlow(
        value = ConfirmResult(
            onCancel = this::cancel,
            onConfirmClick = this::confirm,
            onEditPassword = this::onEditPassword,
            onEditRepeatPassword = this::onEditRepeatPassword,
            disposeOneTimeEvents = this::disposeOneTimeEvents
        )
    )
    val stateFlow: StateFlow<ConfirmResult> = mutableStateFlow

    private fun onEditPassword(password: String) = viewModelScope.launch {
        mutableStateFlow.update(ConfirmResult::hideErrors)
        mutableStateFlow.update { it.copy(password = password) }
    }

    private fun onEditRepeatPassword(password: String) = viewModelScope.launch {
        mutableStateFlow.update(ConfirmResult::hideErrors)
        mutableStateFlow.update { it.copy(repeatPassword = password) }
    }

    private fun confirm() = viewModelScope.launch(context = coroutineDispatchers.io) {
        mutableStateFlow.update(ConfirmResult::showLoading)
        try {
            val password = mutableStateFlow.value.password
            val repeatPassword = mutableStateFlow.value.repeatPassword
            when {
                password != repeatPassword -> mutableStateFlow.update {
                    it.copy(
                        repeatPasswordFieldLabel = FieldLabel.NO_MATCH,
                        isRepeatPasswordError = true
                    )
                }
                password.isEmpty() -> mutableStateFlow.update {
                    it.copy(
                        passwordFieldLabel = FieldLabel.EMPTY,
                        isPasswordError = true
                    )
                }
                else -> {
                    changePasswordUseCase(null, password)
                    withContext(coroutineDispatchers.main) {
                        router.popBackStack()
                    }
                }
            }
        } catch (e: Throwable) {
            Napier.e("❌", e)
            mutableStateFlow.update { it.copy(snackBarMessageType = e.message) }
        } finally {
            mutableStateFlow.update(ConfirmResult::hideLoading)
        }
    }

    private fun cancel() = viewModelScope.launch {
        router.popBackStack()
    }

    private fun disposeOneTimeEvents() = viewModelScope.launch {
        mutableStateFlow.update(ConfirmResult::hideSnackBarMessage)
    }
}
