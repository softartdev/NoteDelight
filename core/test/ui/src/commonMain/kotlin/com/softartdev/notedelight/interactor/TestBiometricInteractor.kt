package com.softartdev.notedelight.interactor

class TestBiometricInteractor : BiometricInteractor {
    var canAuthenticateResult: Boolean = false
        private set
    var storedPassword: CharSequence? = null
        private set
    var encryptResult: BiometricResult = BiometricResult.Success
    var decryptResult: DecryptedPasswordResult? = null
    var clearStoredPasswordCount: Int = 0
        private set

    fun reset(
        canAuthenticateResult: Boolean = false,
        storedPassword: CharSequence? = null,
    ) {
        this.canAuthenticateResult = canAuthenticateResult
        this.storedPassword = storedPassword
        encryptResult = BiometricResult.Success
        decryptResult = null
        clearStoredPasswordCount = 0
    }

    override suspend fun canAuthenticate(): Boolean = canAuthenticateResult

    override suspend fun hasStoredPassword(): Boolean = storedPassword != null

    override suspend fun encryptAndStorePassword(
        password: CharSequence,
        title: String,
        subtitle: String,
        negativeButton: String,
        biometricPlatformWrapper: BiometricPlatformWrapper,
    ): BiometricResult {
        if (encryptResult == BiometricResult.Success) {
            storedPassword = password.toString()
        }
        return encryptResult
    }

    override suspend fun decryptStoredPassword(
        title: String,
        subtitle: String,
        negativeButton: String,
        biometricPlatformWrapper: BiometricPlatformWrapper,
    ): DecryptedPasswordResult = decryptResult
        ?: storedPassword?.let { DecryptedPasswordResult.Success(it) }
        ?: DecryptedPasswordResult.Unavailable

    override suspend fun clearStoredPassword() {
        clearStoredPasswordCount++
        storedPassword = null
    }
}
