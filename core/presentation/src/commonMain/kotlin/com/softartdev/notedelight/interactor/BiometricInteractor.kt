package com.softartdev.notedelight.interactor

expect class BiometricInteractor {

    suspend fun canAuthenticate(): Boolean

    fun hasStoredPassword(): Boolean

    suspend fun encryptAndStorePassword(
        password: CharSequence,
        title: String,
        subtitle: String,
        negativeButton: String,
    ): BiometricResult

    suspend fun decryptStoredPassword(
        title: String,
        subtitle: String,
        negativeButton: String,
    ): DecryptedPasswordResult

    fun clearStoredPassword()
}
