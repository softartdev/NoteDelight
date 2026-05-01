package com.softartdev.notedelight.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.softartdev.notedelight.interactor.BiometricPlatformWrapper

@Composable
actual fun rememberBiometricPlatformWrapper(): BiometricPlatformWrapper = remember {
    return@remember BiometricPlatformWrapper()
}
