package com.softartdev.notedelight.interactor

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidBiometricAuthService(private val context: Context) : BiometricAuthService {

    override suspend fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG
        return biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }

    override suspend fun authenticate(): BiometricAuthResult = suspendCancellableCoroutine { continuation ->
        val activity = context.findActivity() as? FragmentActivity
        if (activity == null) {
            continuation.resume(BiometricAuthResult.FallbackToPassword)
            return@suspendCancellableCoroutine
        }
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric authentication")
            .setSubtitle("Sign in to NoteDelight")
            .setNegativeButtonText("Use password")
            .build()
        val biometricPrompt = BiometricPrompt(
            activity,
            ContextCompat.getMainExecutor(activity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    if (continuation.isActive) {
                        continuation.resume(BiometricAuthResult.Success)
                    }
                }

                override fun onAuthenticationFailed() {
                    if (continuation.isActive) {
                        continuation.resume(BiometricAuthResult.Failed)
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (!continuation.isActive) return
                    val authResult = when (errorCode) {
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_CANCELED -> BiometricAuthResult.FallbackToPassword
                        else -> BiometricAuthResult.Failed
                    }
                    continuation.resume(authResult)
                }
            }
        )
        biometricPrompt.authenticate(promptInfo)
    }
}

private tailrec fun Context.findActivity(): android.app.Activity? = when (this) {
    is android.app.Activity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}
