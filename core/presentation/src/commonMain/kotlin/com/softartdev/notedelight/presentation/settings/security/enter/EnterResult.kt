package com.softartdev.notedelight.presentation.settings.security.enter

import com.softartdev.notedelight.presentation.settings.security.FieldLabel

data class EnterResult(
    val loading: Boolean = false,
    val fieldLabel: FieldLabel = FieldLabel.ENTER,
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isError: Boolean = false,
    val snackBarMessageType: String? = null,
    val onCancel: () -> Unit = {},
    val onEditPassword: (password: String) -> Unit = {},
    val onTogglePasswordVisibility: () -> Unit = {},
    val onEnterClick: () -> Unit = {},
    val disposeOneTimeEvents: () -> Unit = {}
) {
    fun showLoading(): EnterResult = copy(loading = true)
    fun hideLoading(): EnterResult = copy(loading = false)
    fun showError(): EnterResult = copy(isError = true)
    fun hideError(): EnterResult = copy(isError = false)
    fun hideSnackBarMessage(): EnterResult = copy(snackBarMessageType = null)
    fun togglePasswordVisibility(): EnterResult = copy(isPasswordVisible = !isPasswordVisible)
}
