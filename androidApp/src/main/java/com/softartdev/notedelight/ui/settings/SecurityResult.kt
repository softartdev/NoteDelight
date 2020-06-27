package com.softartdev.notedelight.ui.settings

sealed class SecurityResult {
    data class EncryptEnable(val encryption: Boolean) : SecurityResult()
    object PasswordDialog : SecurityResult()
    object SetPasswordDialog : SecurityResult()
    object ChangePasswordDialog : SecurityResult()
    data class Error(val message: String?) : SecurityResult()
}