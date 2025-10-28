package com.softartdev.notedelight.presentation.settings.security.enter

import androidx.compose.ui.autofill.AutofillManager
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
    private val mutableStateFlow: MutableStateFlow<EnterResult> = MutableStateFlow(EnterResult())
    val stateFlow: StateFlow<EnterResult> = mutableStateFlow
    var autofillManager: AutofillManager? = null

    fun onAction(action: EnterAction) = when (action) {
        is EnterAction.Cancel -> cancel()
        is EnterAction.OnEditPassword -> onEditPassword(action.password)
        is EnterAction.TogglePasswordVisibility -> togglePasswordVisibility()
        is EnterAction.OnEnterClick -> enterCheck()
    }

    private fun onEditPassword(password: String) = viewModelScope.launch {
        mutableStateFlow.update(EnterResult::hideError)
        mutableStateFlow.update { it.copy(fieldLabel = FieldLabel.ENTER_PASSWORD) }
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
                    mutableStateFlow.update { it.copy(fieldLabel = FieldLabel.EMPTY_PASSWORD) }
                    mutableStateFlow.update(EnterResult::showError)
                }
                checkPasswordUseCase(password) -> {
                    changePasswordUseCase(password, null)
                    autofillManager?.commit()
                    navigateUp()
                }
                else -> {
                    mutableStateFlow.update { it.copy(fieldLabel = FieldLabel.INCORRECT_PASSWORD) }
                    mutableStateFlow.update(EnterResult::showError)
                }
            }
        } catch (e: Throwable) {
            Napier.e("❌", e)
            autofillManager?.cancel()
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

    fun disposeOneTimeEvents() = viewModelScope.launch {
        mutableStateFlow.update(EnterResult::hideSnackBarMessage)
    }
}
