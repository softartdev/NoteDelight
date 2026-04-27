package com.softartdev.notedelight.interactor

interface BiometricAuthService {
    suspend fun isBiometricAvailable(): Boolean
    suspend fun authenticate(): BiometricAuthResult
}

sealed interface BiometricAuthResult {
    data object Success : BiometricAuthResult
    data object Failed : BiometricAuthResult
    data object FallbackToPassword : BiometricAuthResult
}
