package com.softartdev.notedelight.interactor

expect class BiometricInteractor {

    suspend fun canAuthenticate(): Boolean

    suspend fun hasStoredPassword(): Boolean

    suspend fun encryptAndStorePassword(
        password: CharSequence,
        title: String,
        subtitle: String,
        negativeButton: String,
        biometricPlatformWrapper: BiometricPlatformWrapper,
    ): BiometricResult

    suspend fun decryptStoredPassword(
        title: String,
        subtitle: String,
        negativeButton: String,
        biometricPlatformWrapper: BiometricPlatformWrapper,
    ): DecryptedPasswordResult

    suspend fun clearStoredPassword()
}
