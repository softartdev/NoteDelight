package com.softartdev.notedelight.presentation.signin

interface BiometricAuthenticator {
    fun isAvailable(): Boolean
    suspend fun authenticate(): BiometricAuthResult
}

sealed interface BiometricAuthResult {
    data object Success : BiometricAuthResult
    data object Cancelled : BiometricAuthResult
    data class Error(val throwable: Throwable) : BiometricAuthResult
}

object NoOpBiometricAuthenticator : BiometricAuthenticator {
    override fun isAvailable(): Boolean = false

    override suspend fun authenticate(): BiometricAuthResult = BiometricAuthResult.Cancelled
}
