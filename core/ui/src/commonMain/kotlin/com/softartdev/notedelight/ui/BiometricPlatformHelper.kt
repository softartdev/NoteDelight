package com.softartdev.notedelight.ui

import androidx.compose.runtime.Composable
import com.softartdev.notedelight.interactor.BiometricPlatformWrapper

/**
 * Returns a [BiometricPlatformWrapper] bound to the current composition context.
 *
 * On Android the wrapper carries `LocalActivity.current` (cast to `FragmentActivity`) so that
 * [com.softartdev.notedelight.interactor.BiometricInteractor] can create a
 * `BiometricPrompt` against the correct host.
 *
 * On all other platforms (iOS/Desktop/Web) biometrics are handled natively without an Android
 * Activity, so the returned [BiometricPlatformWrapper] is an empty stub.
 */
@Composable
expect fun rememberBiometricPlatformWrapper(): BiometricPlatformWrapper
