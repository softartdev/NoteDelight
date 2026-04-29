package com.softartdev.notedelight.presentation.signin

data class SignInResult(
    val state: State = State.Form,
    val biometricVisible: Boolean = false,
) {
    sealed interface State {
        data object Form : State

        data object Progress : State

        sealed interface Error : State {
            data object EmptyPass : Error

            data object IncorrectPass : Error

            data object Biometric : Error
        }
    }
}
