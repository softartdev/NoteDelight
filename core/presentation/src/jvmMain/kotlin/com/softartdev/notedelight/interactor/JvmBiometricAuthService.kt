package com.softartdev.notedelight.interactor

class JvmBiometricAuthService : BiometricAuthService {
    override suspend fun isBiometricAvailable(): Boolean = false

    override suspend fun authenticate(): BiometricAuthResult = BiometricAuthResult.FallbackToPassword
}
