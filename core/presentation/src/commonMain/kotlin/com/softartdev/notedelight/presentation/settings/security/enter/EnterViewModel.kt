package com.softartdev.notedelight.presentation.settings.security.enter

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

class EnterViewModel(
    private val checkPasswordUseCase: CheckPasswordUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val router: Router,
    private val coroutineDispatchers: CoroutineDispatchers,
) : ViewModel() {
    private val mutableStateFlow: MutableStateFlow<EnterResult> = MutableStateFlow(
        value = EnterResult(
            onCancel = this::cancel,
            onEnterClick = this::enterCheck,
            onEditPassword = this::onEditPassword,
            onTogglePasswordVisibility = this::togglePasswordVisibility,
            disposeOneTimeEvents = this::disposeOneTimeEvents
        )
    )
    val stateFlow: StateFlow<EnterResult> = mutableStateFlow

    private fun onEditPassword(password: String) = viewModelScope.launch {
        mutableStateFlow.update(EnterResult::hideError)
        mutableStateFlow.update { it.copy(fieldLabel = FieldLabel.ENTER) }
        mutableStateFlow.update { it.copy(password = password) }
    }

    private fun togglePasswordVisibility() = viewModelScope.launch {
        mutableStateFlow.update(EnterResult::togglePasswordVisibility)
    }

    private fun enterCheck() = viewModelScope.launch(context = coroutineDispatchers.io) {
        mutableStateFlow.update(EnterResult::showLoading)
        try {
            val password = mutableStateFlow.value.password
            when {
                password.isEmpty() -> {
                    mutableStateFlow.update { it.copy(fieldLabel = FieldLabel.EMPTY) }
                    mutableStateFlow.update(EnterResult::showError)
                }
                checkPasswordUseCase(password) -> {
                    changePasswordUseCase(password, null)
                    navigateUp()
                }
                else -> {
                    mutableStateFlow.update { it.copy(fieldLabel = FieldLabel.INCORRECT) }
                    mutableStateFlow.update(EnterResult::showError)
                }
            }
        } catch (e: Throwable) {
            Napier.e("❌", e)
            mutableStateFlow.update { it.copy(snackBarMessageType = e.message) }
        } finally {
            mutableStateFlow.update(EnterResult::hideLoading)
        }
    }

    private fun cancel() = viewModelScope.launch {
        router.popBackStack()
    }

    private fun navigateUp() = viewModelScope.launch {
        router.popBackStack()
    }

    private fun disposeOneTimeEvents() = viewModelScope.launch {
        mutableStateFlow.update(EnterResult::hideSnackBarMessage)
    }
}
