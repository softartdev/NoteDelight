package com.softartdev.notedelight.presentation.signin

data class SignInResult(
    val loading: Boolean = false,
    val errorType: ErrorType? = null,
    val biometricVisible: Boolean = false,
) {
    enum class ErrorType { EMPTY_PASSWORD, INCORRECT_PASSWORD }

    fun showLoading(): SignInResult = copy(loading = true)
    fun hideLoading(): SignInResult = copy(loading = false)
    fun hideBiometric(): SignInResult = copy(biometricVisible = true)
    fun showEmptyPasswordError(): SignInResult = copy(errorType = ErrorType.EMPTY_PASSWORD)
    fun showIncorrectPasswordError(): SignInResult = copy(errorType = ErrorType.INCORRECT_PASSWORD)
    fun hideErrors(): SignInResult = copy(errorType = null)
}
