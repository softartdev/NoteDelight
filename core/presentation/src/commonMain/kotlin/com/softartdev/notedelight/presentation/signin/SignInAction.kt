package com.softartdev.notedelight.presentation.signin

import com.softartdev.notedelight.interactor.BiometricPlatformWrapper

sealed interface SignInAction {
    data object OnSettingsClick : SignInAction
    data class OnSignInClick(val pass: CharSequence) : SignInAction
    data object RefreshBiometric : SignInAction
    data class OnBiometricClick(
        val title: String,
        val subtitle: String,
        val negativeButton: String,
        val biometricPlatformWrapper: BiometricPlatformWrapper
    ) : SignInAction
}
