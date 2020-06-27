package com.softartdev.notedelight.ui.settings.security.enter

sealed class EnterResult {
    object Loading: EnterResult()
    object Success: EnterResult()
    object EmptyPasswordError: EnterResult()
    object IncorrectPasswordError: EnterResult()
    data class Error(val message: String?): EnterResult()
}