package com.softartdev.notedelight.interactor

class JvmBiometricInteractor : BiometricInteractor {

    override suspend fun canAuthenticate(): Boolean = false

    override suspend fun hasStoredPassword(): Boolean = false

    override suspend fun encryptAndStorePassword(
        password: CharSequence,
        title: String,
        subtitle: String,
        negativeButton: String,
        biometricPlatformWrapper: BiometricPlatformWrapper,
    ): BiometricResult = BiometricResult.Unavailable

    override suspend fun decryptStoredPassword(
        title: String,
        subtitle: String,
        negativeButton: String,
        biometricPlatformWrapper: BiometricPlatformWrapper,
    ): DecryptedPasswordResult = DecryptedPasswordResult.Unavailable

    override suspend fun clearStoredPassword() = Unit
}
