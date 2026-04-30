package com.softartdev.notedelight.presentation.signin

data class SignInResult(
    val loading: Boolean = false,
    val errorType: ErrorType? = null,
    val biometricVisible: Boolean = false,
)

enum class ErrorType {
    EMPTY_PASSWORD,
    INCORRECT_PASSWORD,
}
