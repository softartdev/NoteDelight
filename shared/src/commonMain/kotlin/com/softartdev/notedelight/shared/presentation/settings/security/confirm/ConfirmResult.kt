package com.softartdev.notedelight.shared.presentation.settings.security.confirm

sealed class ConfirmResult {
    object InitState: ConfirmResult()
    object Loading: ConfirmResult()
    object Success: ConfirmResult()
    object PasswordsNoMatchError: ConfirmResult()
    object EmptyPasswordError: ConfirmResult()
    data class Error(val message: String?): ConfirmResult()
}