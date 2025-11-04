package com.softartdev.notedelight.presentation.settings.security.enter

import com.softartdev.notedelight.presentation.settings.security.FieldLabel

data class EnterResult(
    val loading: Boolean = false,
    val fieldLabel: FieldLabel = FieldLabel.ENTER_PASSWORD,
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isError: Boolean = false
) {
    fun showLoading(): EnterResult = copy(loading = true)
    fun hideLoading(): EnterResult = copy(loading = false)
    fun showError(): EnterResult = copy(isError = true)
    fun hideError(): EnterResult = copy(isError = false)
    fun togglePasswordVisibility(): EnterResult = copy(isPasswordVisible = !isPasswordVisible)
}

sealed interface EnterAction {
    data object Cancel : EnterAction
    data class OnEditPassword(val password: String) : EnterAction
    data object TogglePasswordVisibility : EnterAction
    data object OnEnterClick : EnterAction
}
