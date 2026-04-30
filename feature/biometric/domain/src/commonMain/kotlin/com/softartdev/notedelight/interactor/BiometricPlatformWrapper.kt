package com.softartdev.notedelight.interactor

/**
 * Platform-agnostic wrapper that carries the current host Activity (Android) or nothing
 * (iOS/Desktop/Web). Created from a `@Composable` context via `rememberActivityProvider()`
 * in `core:ui` and forwarded to [BiometricInteractor] methods that need to show the system
 * biometric prompt.
 */
expect class BiometricPlatformWrapper
