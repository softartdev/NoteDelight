package com.softartdev.notedelight.interactor

sealed interface BiometricResult {
    data object Success : BiometricResult
    data object Failed : BiometricResult
    data object Cancelled : BiometricResult
    data object Unavailable : BiometricResult
    data class Error(val message: String) : BiometricResult
}

sealed interface DecryptedPasswordResult {
    data class Success(val password: CharSequence) : DecryptedPasswordResult
    data class Failure(val result: BiometricResult) : DecryptedPasswordResult
}
