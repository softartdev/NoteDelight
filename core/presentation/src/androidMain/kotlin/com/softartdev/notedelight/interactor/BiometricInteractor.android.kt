package com.softartdev.notedelight.interactor

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlin.coroutines.resume

actual class BiometricInteractor(
    private val context: Context,
    private val activityHolder: BiometricActivityHolder,
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    actual suspend fun canAuthenticate(): Boolean {
        val mgr = BiometricManager.from(context)
        return mgr.canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }

    actual fun hasStoredPassword(): Boolean =
        prefs.contains(KEY_CIPHERTEXT) && prefs.contains(KEY_IV)

    actual suspend fun encryptAndStorePassword(
        password: CharSequence,
        title: String,
        subtitle: String,
        negativeButton: String,
    ): BiometricResult {
        val activity = activityHolder.current()
            ?: return BiometricResult.Error("No active Activity for BiometricPrompt")
        clearStoredPassword()
        val secretKey = try {
            createOrGetKey()
        } catch (t: Throwable) {
            return BiometricResult.Error(t.message ?: "Keystore failure")
        }
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, secretKey)
        }
        return when (val auth = runPrompt(activity, cipher, title, subtitle, negativeButton)) {
            is PromptOutcome.Authenticated -> {
                val out = auth.cipher.doFinal(password.toString().toByteArray(Charsets.UTF_8))
                prefs.edit()
                    .putString(KEY_CIPHERTEXT, Base64.encodeToString(out, Base64.NO_WRAP))
                    .putString(KEY_IV, Base64.encodeToString(auth.cipher.iv, Base64.NO_WRAP))
                    .apply()
                BiometricResult.Success
            }
            is PromptOutcome.Failure -> auth.result
        }
    }

    actual suspend fun decryptStoredPassword(
        title: String,
        subtitle: String,
        negativeButton: String,
    ): DecryptedPasswordResult {
        if (!hasStoredPassword()) {
            return DecryptedPasswordResult.Failure(BiometricResult.Unavailable)
        }
        val activity = activityHolder.current()
            ?: return DecryptedPasswordResult.Failure(
                BiometricResult.Error("No active Activity for BiometricPrompt")
            )
        val ciphertext = Base64.decode(prefs.getString(KEY_CIPHERTEXT, null), Base64.NO_WRAP)
        val iv = Base64.decode(prefs.getString(KEY_IV, null), Base64.NO_WRAP)
        val secretKey = try {
            existingKey() ?: run {
                clearStoredPassword()
                return DecryptedPasswordResult.Failure(BiometricResult.Unavailable)
            }
        } catch (t: KeyPermanentlyInvalidatedException) {
            clearStoredPassword()
            return DecryptedPasswordResult.Failure(BiometricResult.Unavailable)
        } catch (t: Throwable) {
            return DecryptedPasswordResult.Failure(
                BiometricResult.Error(t.message ?: "Keystore failure")
            )
        }
        val cipher = try {
            Cipher.getInstance(TRANSFORMATION).apply {
                init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_BITS, iv))
            }
        } catch (t: KeyPermanentlyInvalidatedException) {
            clearStoredPassword()
            return DecryptedPasswordResult.Failure(BiometricResult.Unavailable)
        } catch (t: Throwable) {
            return DecryptedPasswordResult.Failure(
                BiometricResult.Error(t.message ?: "Cipher init failed")
            )
        }
        return when (val auth = runPrompt(activity, cipher, title, subtitle, negativeButton)) {
            is PromptOutcome.Authenticated -> {
                val plain = auth.cipher.doFinal(ciphertext)
                DecryptedPasswordResult.Success(plain.toString(Charsets.UTF_8))
            }
            is PromptOutcome.Failure -> DecryptedPasswordResult.Failure(auth.result)
        }
    }

    actual fun clearStoredPassword() {
        prefs.edit().remove(KEY_CIPHERTEXT).remove(KEY_IV).apply()
        runCatching {
            KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }.deleteEntry(KEY_ALIAS)
        }
    }

    private fun existingKey(): SecretKey? {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        return keyStore.getKey(KEY_ALIAS, null) as? SecretKey
    }

    private fun createOrGetKey(): SecretKey {
        existingKey()?.let { return it }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(true)
            .setInvalidatedByBiometricEnrollment(true)
            .build()
        generator.init(spec)
        return generator.generateKey()
    }

    private suspend fun runPrompt(
        activity: androidx.appcompat.app.AppCompatActivity,
        cipher: Cipher,
        title: String,
        subtitle: String,
        negativeButton: String,
    ): PromptOutcome = suspendCancellableCoroutine { continuation ->
        val executor = ContextCompat.getMainExecutor(context)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                val resultCipher = result.cryptoObject?.cipher
                if (resultCipher == null) {
                    continuation.resume(
                        PromptOutcome.Failure(BiometricResult.Error("Missing CryptoObject"))
                    )
                } else {
                    continuation.resume(PromptOutcome.Authenticated(resultCipher))
                }
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                val mapped = when (errorCode) {
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                    BiometricPrompt.ERROR_CANCELED -> BiometricResult.Cancelled
                    BiometricPrompt.ERROR_NO_BIOMETRICS,
                    BiometricPrompt.ERROR_HW_NOT_PRESENT,
                    BiometricPrompt.ERROR_HW_UNAVAILABLE -> BiometricResult.Unavailable
                    else -> BiometricResult.Error(errString.toString())
                }
                continuation.resume(PromptOutcome.Failure(mapped))
            }

            override fun onAuthenticationFailed() {
                // Triggered on a wrong fingerprint; system gives the user another try, so do not
                // resume the continuation here. The terminal callback is onAuthenticationError.
            }
        }
        val prompt = BiometricPrompt(activity, executor, callback)
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(negativeButton)
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .build()
        prompt.authenticate(info, BiometricPrompt.CryptoObject(cipher))
        continuation.invokeOnCancellation { runCatching { prompt.cancelAuthentication() } }
    }

    private sealed interface PromptOutcome {
        data class Authenticated(val cipher: Cipher) : PromptOutcome
        data class Failure(val result: BiometricResult) : PromptOutcome
    }

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "notedelight_biometric_key"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_TAG_BITS = 128
        private const val PREFS_NAME = "notedelight_biometric_prefs"
        private const val KEY_CIPHERTEXT = "ciphertext"
        private const val KEY_IV = "iv"
    }
}
