package com.softartdev.notedelight.interactor

import platform.Foundation.NSUserDefaults

actual class BiometricInteractor {
    actual var biometricEnabled: Boolean
        get() = NSUserDefaults.standardUserDefaults.boolForKey(KEY_BIOMETRIC_ENABLED)
        set(value) {
            NSUserDefaults.standardUserDefaults.setBool(value, KEY_BIOMETRIC_ENABLED)
        }

    actual var biometricConfirmed: Boolean
        get() = NSUserDefaults.standardUserDefaults.boolForKey(KEY_BIOMETRIC_CONFIRMED)
        set(value) {
            NSUserDefaults.standardUserDefaults.setBool(value, KEY_BIOMETRIC_CONFIRMED)
        }

    actual fun capability(): BiometricCapability = BiometricCapability(
        available = false,
        enrolled = false,
    )

    private companion object {
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_BIOMETRIC_CONFIRMED = "biometric_confirmed"
    }
}
