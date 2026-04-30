package com.softartdev.notedelight.ui

import androidx.compose.runtime.Composable
import com.softartdev.notedelight.interactor.BiometricPlatformWrapper

@Composable
actual fun rememberBiometricPlatformWrapper(): BiometricPlatformWrapper = BiometricPlatformWrapper()
