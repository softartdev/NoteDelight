package com.softartdev.notedelight.interactor

import android.app.KeyguardManager
import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Build

actual class BiometricInteractor(private val context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    actual var biometricEnabled: Boolean
        get() = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
        set(value) {
            prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, value).apply()
        }

    actual var biometricConfirmed: Boolean
        get() = prefs.getBoolean(KEY_BIOMETRIC_CONFIRMED, false)
        set(value) {
            prefs.edit().putBoolean(KEY_BIOMETRIC_CONFIRMED, value).apply()
        }

    actual fun capability(): BiometricCapability {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return BiometricCapability(available = false, enrolled = false)
        }
        val fingerprintManager = context.getSystemService(Context.FINGERPRINT_SERVICE) as? FingerprintManager
            ?: return BiometricCapability(available = false, enrolled = false)
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        val available = fingerprintManager.isHardwareDetected && (keyguardManager?.isKeyguardSecure == true)
        val enrolled = available && fingerprintManager.hasEnrolledFingerprints()
        return BiometricCapability(available = available, enrolled = enrolled)
    }

    private companion object {
        private const val PREFS_NAME = "notedelight_settings"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_BIOMETRIC_CONFIRMED = "biometric_confirmed"
    }
}
