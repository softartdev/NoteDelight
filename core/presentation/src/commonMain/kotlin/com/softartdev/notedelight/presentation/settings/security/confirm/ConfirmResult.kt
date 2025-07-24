package com.softartdev.notedelight.presentation.settings.security.confirm

import com.softartdev.notedelight.presentation.settings.security.FieldLabel

data class ConfirmResult(
    val loading: Boolean = false,
    val password: String = "",
    val repeatPassword: String = "",
    val passwordFieldLabel: FieldLabel = FieldLabel.ENTER_PASSWORD,
    val repeatPasswordFieldLabel: FieldLabel = FieldLabel.CONFIRM_PASSWORD,
    val isPasswordError: Boolean = false,
    val isRepeatPasswordError: Boolean = false,
    val snackBarMessageType: String? = null,
    val onCancel: () -> Unit = {},
    val onEditPassword: (password: String) -> Unit = {},
    val onEditRepeatPassword: (password: String) -> Unit = {},
    val onConfirmClick: () -> Unit = {},
    val disposeOneTimeEvents: () -> Unit = {}
) {
    fun showLoading(): ConfirmResult = copy(loading = true)
    fun hideLoading(): ConfirmResult = copy(loading = false)
    fun hideErrors(): ConfirmResult = copy(
        isPasswordError = false,
        isRepeatPasswordError = false,
        passwordFieldLabel = FieldLabel.ENTER_PASSWORD,
        repeatPasswordFieldLabel = FieldLabel.CONFIRM_PASSWORD
    )
    fun hideSnackBarMessage(): ConfirmResult = copy(snackBarMessageType = null)
}
