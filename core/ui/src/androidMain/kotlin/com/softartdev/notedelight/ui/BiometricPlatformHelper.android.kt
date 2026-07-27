package com.softartdev.notedelight.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.fragment.app.FragmentActivity
import com.softartdev.notedelight.interactor.BiometricPlatformWrapper

@Composable
actual fun rememberBiometricPlatformWrapper(): BiometricPlatformWrapper {
    val fragmentActivity: FragmentActivity = when (LocalInspectionMode.current) {
        true -> FragmentActivity() // Create a new FragmentActivity instance for preview mode
        false -> LocalActivity.current as FragmentActivity
    }
    return remember(key1 = fragmentActivity) { BiometricPlatformWrapper(fragmentActivity) }
}
