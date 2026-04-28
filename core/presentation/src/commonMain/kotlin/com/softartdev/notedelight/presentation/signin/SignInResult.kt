package com.softartdev.notedelight.presentation.signin

sealed interface SignInResult {

    val biometricVisible: Boolean

    data class Form(override val biometricVisible: Boolean = false) : SignInResult

    data class Progress(override val biometricVisible: Boolean = false) : SignInResult

    sealed interface Error : SignInResult {

        data class EmptyPass(override val biometricVisible: Boolean = false) : Error

        data class IncorrectPass(override val biometricVisible: Boolean = false) : Error

        data class Biometric(override val biometricVisible: Boolean = false) : Error
    }
}
