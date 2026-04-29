package com.softartdev.notedelight.interactor

actual class BiometricInteractor {
    actual suspend fun canAuthenticate(): Boolean = false

    actual suspend fun hasStoredPassword(): Boolean = false

    actual suspend fun encryptAndStorePassword(
        password: CharSequence,
        title: String,
        subtitle: String,
        negativeButton: String,
    ): BiometricResult = BiometricResult.Unavailable

    actual suspend fun decryptStoredPassword(
        title: String,
        subtitle: String,
        negativeButton: String,
    ): DecryptedPasswordResult = DecryptedPasswordResult.Failure(BiometricResult.Unavailable)

    actual suspend fun clearStoredPassword() = Unit
}
