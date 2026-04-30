package com.softartdev.notedelight.interactor

actual class BiometricInteractor {
    actual suspend fun canAuthenticate(): Boolean = false

    actual suspend fun hasStoredPassword(): Boolean = false

    actual suspend fun encryptAndStorePassword(
        password: CharSequence,
        title: String,
        subtitle: String,
        negativeButton: String,
        biometricPlatformWrapper: BiometricPlatformWrapper,
    ): BiometricResult = BiometricResult.Unavailable

    actual suspend fun decryptStoredPassword(
        title: String,
        subtitle: String,
        negativeButton: String,
        biometricPlatformWrapper: BiometricPlatformWrapper,
    ): DecryptedPasswordResult = DecryptedPasswordResult.Unavailable

    actual suspend fun clearStoredPassword() = Unit
}
