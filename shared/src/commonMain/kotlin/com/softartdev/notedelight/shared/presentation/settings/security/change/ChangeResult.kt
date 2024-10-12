package com.softartdev.notedelight.shared.presentation.settings.security.change

sealed class ChangeResult {
    data object InitState: ChangeResult()
    data object Loading: ChangeResult()
    data object OldEmptyPasswordError: ChangeResult()
    data object NewEmptyPasswordError: ChangeResult()
    data object PasswordsNoMatchError: ChangeResult()
    data object IncorrectPasswordError: ChangeResult()
    data class Error(val message: String?): ChangeResult()
}
