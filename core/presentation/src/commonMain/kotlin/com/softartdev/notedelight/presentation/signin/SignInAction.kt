package com.softartdev.notedelight.presentation.signin

sealed interface SignInAction {
    data object OnSettingsClick : SignInAction
    data class OnSignInClick(val pass: CharSequence) : SignInAction
}
