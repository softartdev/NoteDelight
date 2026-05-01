package com.softartdev.notedelight.interactor

import kotlinx.coroutines.channels.Channel

interface BiometricInteractor {

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

    companion object {
        val disableDialogChannel: Channel<Boolean> by lazy { Channel() }
    }
}
