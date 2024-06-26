package com.softartdev.notedelight.shared.presentation.signin

sealed class SignInResult {
    object ShowSignInForm : SignInResult()
    object ShowProgress : SignInResult()
    object NavMain : SignInResult()
    object ShowEmptyPassError : SignInResult()
    object ShowIncorrectPassError : SignInResult()
    data class ShowError(val error: Throwable) : SignInResult()
}
