package com.softartdev.notedelight.ui.settings.security.confirm

sealed class ConfirmResult {
    object Loading: ConfirmResult()
    object Success: ConfirmResult()
    object PasswordsNoMatchError: ConfirmResult()
    object EmptyPasswordError: ConfirmResult()
    data class Error(val message: String?): ConfirmResult()
}