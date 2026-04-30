package com.softartdev.notedelight.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentActivity
import com.softartdev.notedelight.interactor.BiometricPlatformWrapper

@Composable
actual fun rememberBiometricPlatformWrapper(): BiometricPlatformWrapper {
    val activity = LocalActivity.current as? FragmentActivity
        ?: error("rememberBiometricPlatformWrapper must be called within a FragmentActivity-hosted composition")
    return BiometricPlatformWrapper(activity)
}
