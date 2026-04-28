package com.softartdev.notedelight.presentation.signin

data class SignInResult(
    val state: State = State.ShowSignInForm,
    val biometricVisible: Boolean = false,
) {
    val isError: Boolean
        get() = state.isError

    enum class State(val isError: Boolean = false) {
        ShowSignInForm,
        ShowProgress,
        ShowEmptyPassError(isError = true),
        ShowIncorrectPassError(isError = true),
        ShowBiometricError(isError = true),
    }
}
