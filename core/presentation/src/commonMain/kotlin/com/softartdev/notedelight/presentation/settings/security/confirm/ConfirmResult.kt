package com.softartdev.notedelight.presentation.settings.security.confirm

import com.softartdev.notedelight.presentation.settings.security.FieldLabel

data class ConfirmResult(
    val loading: Boolean = false,
    val password: String = "",
    val repeatPassword: String = "",
    val passwordFieldLabel: FieldLabel = FieldLabel.ENTER_PASSWORD,
    val repeatPasswordFieldLabel: FieldLabel = FieldLabel.CONFIRM_PASSWORD,
    val isPasswordError: Boolean = false,
    val isRepeatPasswordError: Boolean = false
) {
    fun showLoading(): ConfirmResult = copy(loading = true)
    fun hideLoading(): ConfirmResult = copy(loading = false)
    fun hideErrors(): ConfirmResult = copy(
        isPasswordError = false,
        isRepeatPasswordError = false,
        passwordFieldLabel = FieldLabel.ENTER_PASSWORD,
        repeatPasswordFieldLabel = FieldLabel.CONFIRM_PASSWORD
    )
}

sealed interface ConfirmAction {
    data object Cancel : ConfirmAction
    data class OnEditPassword(val password: String) : ConfirmAction
    data class OnEditRepeatPassword(val password: String) : ConfirmAction
    data object OnConfirmClick : ConfirmAction
}
