package com.softartdev.notedelight.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.fragment.app.FragmentActivity
import com.softartdev.notedelight.interactor.BiometricPlatformWrapper

@Composable
actual fun rememberBiometricPlatformWrapper(): BiometricPlatformWrapper {
    val fragmentActivity = LocalActivity.current as FragmentActivity
    return remember(key1 = fragmentActivity) { BiometricPlatformWrapper(fragmentActivity) }
}
