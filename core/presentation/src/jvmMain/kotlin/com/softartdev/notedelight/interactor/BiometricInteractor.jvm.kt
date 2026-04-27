package com.softartdev.notedelight.interactor

import java.util.prefs.Preferences

actual class BiometricInteractor {
    private val preferences = Preferences.userRoot().node(PREFS_NODE)

    actual var biometricEnabled: Boolean
        get() = preferences.getBoolean(KEY_BIOMETRIC_ENABLED, false)
        set(value) {
            preferences.putBoolean(KEY_BIOMETRIC_ENABLED, value)
        }

    actual var biometricConfirmed: Boolean
        get() = preferences.getBoolean(KEY_BIOMETRIC_CONFIRMED, false)
        set(value) {
            preferences.putBoolean(KEY_BIOMETRIC_CONFIRMED, value)
        }

    actual fun capability(): BiometricCapability = BiometricCapability(
        available = false,
        enrolled = false,
    )

    private companion object {
        private const val PREFS_NODE = "com.softartdev.notedelight.settings"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_BIOMETRIC_CONFIRMED = "biometric_confirmed"
    }
}
