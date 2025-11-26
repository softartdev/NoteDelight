package com.softartdev.notedelight.presentation.settings.security.confirm

import androidx.compose.ui.autofill.AutofillManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.interactor.SnackbarInteractor
import com.softartdev.notedelight.interactor.SnackbarMessage
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.presentation.settings.security.FieldLabel
import com.softartdev.notedelight.usecase.crypt.ChangePasswordUseCase
import com.softartdev.notedelight.util.CoroutineDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfirmViewModel(
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val snackbarInteractor: SnackbarInteractor,
    private val router: Router,
    private val coroutineDispatchers: CoroutineDispatchers,
) : ViewModel() {
    private val logger = Logger.withTag(this@ConfirmViewModel::class.simpleName.toString())
    private val mutableStateFlow: MutableStateFlow<ConfirmResult> = MutableStateFlow(
        value = ConfirmResult()
    )
    val stateFlow: StateFlow<ConfirmResult> = mutableStateFlow
    var autofillManager: AutofillManager? = null

    fun onAction(action: ConfirmAction) = when (action) {
        is ConfirmAction.Cancel -> cancel()
        is ConfirmAction.OnEditPassword -> onEditPassword(action.password)
        is ConfirmAction.OnEditRepeatPassword -> onEditRepeatPassword(action.password)
        is ConfirmAction.OnConfirmClick -> confirm()
    }

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
                        repeatPasswordFieldLabel = FieldLabel.PASSWORDS_NOT_MATCH,
                        isRepeatPasswordError = true
                    )
                }
                password.isEmpty() -> mutableStateFlow.update {
                    it.copy(
                        passwordFieldLabel = FieldLabel.EMPTY_PASSWORD,
                        isPasswordError = true
                    )
                }
                else -> {
                    changePasswordUseCase(null, password)
                    autofillManager?.commit()
                    withContext(coroutineDispatchers.main) {
                        router.popBackStack()
                    }
                }
            }
        } catch (e: Throwable) {
            logger.e(e) { "Error confirming password" }
            autofillManager?.cancel()
            e.message?.let { snackbarInteractor.showMessage(SnackbarMessage.Simple(it)) }
        } finally {
            mutableStateFlow.update(ConfirmResult::hideLoading)
        }
    }

    private fun cancel() = viewModelScope.launch {
        router.popBackStack()
    }
}