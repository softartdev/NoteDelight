package com.softartdev.notedelight.interactor

import kotlinx.browser.window

actual class BiometricInteractor {
    actual var biometricEnabled: Boolean
        get() = localStorageItem(KEY_BIOMETRIC_ENABLED) == TRUE_VALUE
        set(value) {
            setLocalStorageItem(KEY_BIOMETRIC_ENABLED, value.toString())
        }

    actual var biometricConfirmed: Boolean
        get() = localStorageItem(KEY_BIOMETRIC_CONFIRMED) == TRUE_VALUE
        set(value) {
            setLocalStorageItem(KEY_BIOMETRIC_CONFIRMED, value.toString())
        }

    actual fun capability(): BiometricCapability = BiometricCapability(
        available = false,
        enrolled = false,
    )

    private companion object {
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_BIOMETRIC_CONFIRMED = "biometric_confirmed"
        private const val TRUE_VALUE = "true"
    }
}

private fun localStorageItem(key: String): String? = window.localStorage.getItem(key)

private fun setLocalStorageItem(key: String, value: String) = window.localStorage.setItem(key, value)
