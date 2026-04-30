package com.softartdev.notedelight.presentation.settings.security.biometric

import com.softartdev.notedelight.interactor.BiometricPlatformWrapper
import com.softartdev.notedelight.presentation.settings.security.FieldLabel

data class BiometricEnrollResult(
    val loading: Boolean = false,
    val fieldLabel: FieldLabel = FieldLabel.ENTER_PASSWORD,
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isError: Boolean = false,
) {
    fun showLoading(): BiometricEnrollResult = copy(loading = true)
    fun hideLoading(): BiometricEnrollResult = copy(loading = false)
    fun showError(): BiometricEnrollResult = copy(isError = true)
    fun togglePasswordVisibility(): BiometricEnrollResult = copy(isPasswordVisible = !isPasswordVisible)
}

sealed interface BiometricEnrollAction {
    data object Cancel : BiometricEnrollAction
    data class OnEditPassword(val password: String) : BiometricEnrollAction
    data object TogglePasswordVisibility : BiometricEnrollAction
    data class OnEnrollClick(
        val title: String,
        val subtitle: String,
        val negativeButton: String,
        val biometricPlatformWrapper: BiometricPlatformWrapper? = null,
    ) : BiometricEnrollAction
}
