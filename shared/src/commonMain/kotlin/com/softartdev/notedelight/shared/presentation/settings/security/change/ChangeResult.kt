package com.softartdev.notedelight.shared.presentation.settings.security.change

sealed class ChangeResult {
    object InitState: ChangeResult()
    object Loading: ChangeResult()
    object Success: ChangeResult()
    object OldEmptyPasswordError: ChangeResult()
    object NewEmptyPasswordError: ChangeResult()
    object PasswordsNoMatchError: ChangeResult()
    object IncorrectPasswordError: ChangeResult()
    data class Error(val message: String?): ChangeResult()
}
