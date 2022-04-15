package com.softartdev.notedelight.shared.presentation.settings.security.enter

sealed class EnterResult {
    object InitState: EnterResult()
    object Loading: EnterResult()
    object Success: EnterResult()
    object EmptyPasswordError: EnterResult()
    object IncorrectPasswordError: EnterResult()
    data class Error(val message: String?): EnterResult()
}