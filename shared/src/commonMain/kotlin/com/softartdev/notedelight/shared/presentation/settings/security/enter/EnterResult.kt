package com.softartdev.notedelight.shared.presentation.settings.security.enter

sealed class EnterResult {
    data object InitState: EnterResult()
    data object Loading: EnterResult()
    data object EmptyPasswordError: EnterResult()
    data object IncorrectPasswordError: EnterResult()
    data class Error(val message: String?): EnterResult()
}