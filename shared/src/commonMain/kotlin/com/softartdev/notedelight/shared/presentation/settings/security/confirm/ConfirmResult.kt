package com.softartdev.notedelight.shared.presentation.settings.security.confirm

sealed class ConfirmResult {
    data object InitState: ConfirmResult()
    data object Loading: ConfirmResult()
    data object PasswordsNoMatchError: ConfirmResult()
    data object EmptyPasswordError: ConfirmResult()
    data class Error(val message: String?): ConfirmResult()
}