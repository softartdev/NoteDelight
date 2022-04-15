package com.softartdev.notedelight.shared.presentation.settings

sealed class SecurityResult {
    object Loading : SecurityResult()
    data class EncryptEnable(val encryption: Boolean) : SecurityResult()
    object PasswordDialog : SecurityResult()
    object SetPasswordDialog : SecurityResult()
    object ChangePasswordDialog : SecurityResult()
    data class Error(val message: String?) : SecurityResult()
}