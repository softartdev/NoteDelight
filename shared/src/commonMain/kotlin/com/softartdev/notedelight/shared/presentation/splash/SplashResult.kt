package com.softartdev.notedelight.shared.presentation.splash

sealed class SplashResult {
    object Loading : SplashResult()
    object NavSignIn : SplashResult()
    object NavMain : SplashResult()
    data class ShowError(val message: String?) : SplashResult()
}