package com.softartdev.notedelight.ui.settings.security.change

sealed class ChangeResult {
    object Loading: ChangeResult()
    object Success: ChangeResult()
    object OldEmptyPasswordError: ChangeResult()
    object NewEmptyPasswordError: ChangeResult()
    object PasswordsNoMatchError: ChangeResult()
    object IncorrectPasswordError: ChangeResult()
    data class Error(val message: String?): ChangeResult()
}
