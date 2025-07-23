package com.softartdev.notedelight.presentation.signin

enum class SignInResult(val isError: Boolean = false) {
    ShowSignInForm,
    ShowProgress,
    ShowEmptyPassError(isError = true),
    ShowIncorrectPassError(isError = true)
}
