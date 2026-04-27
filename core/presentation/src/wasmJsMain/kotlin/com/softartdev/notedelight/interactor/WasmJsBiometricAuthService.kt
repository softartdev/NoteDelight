package com.softartdev.notedelight.interactor

class WasmJsBiometricAuthService : BiometricAuthService {
    override suspend fun isBiometricAvailable(): Boolean = false

    override suspend fun authenticate(): BiometricAuthResult = BiometricAuthResult.FallbackToPassword
}
