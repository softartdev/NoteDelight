package com.softartdev.notedelight.interactor

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlin.coroutines.resume

class AndroidBiometricInteractor(context: Context) : BiometricInteractor {
    private val logger = Logger.withTag("BiometricInteractor")
    private val appContext: Context = context.applicationContext
    private val credentialsStore = BiometricCredentialsStore(appContext)

    override suspend fun canAuthenticate(): Boolean = BiometricManager
        .from(appContext)
        .canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS

    override suspend fun hasStoredPassword(): Boolean = credentialsStore.hasCredentials()

    override suspend fun encryptAndStorePassword(
        password: CharSequence,
        title: String,
        subtitle: String,
        negativeButton: String,
        biometricPlatformWrapper: BiometricPlatformWrapper,
    ): BiometricResult {
        val activity: FragmentActivity = biometricPlatformWrapper.activity
        clearStoredPassword()
        val secretKey: SecretKey = try {
            createOrGetKey()
        } catch (t: Throwable) {
            logger.e(t) { "Keystore failure" }
            return BiometricResult.Error(t.message ?: "Keystore failure")
        }
        val cipher: Cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, secretKey)
        }
        return when (val auth: PromptOutcome = runPrompt(activity, cipher, title, subtitle, negativeButton)) {
            is PromptOutcome.Authenticated -> {
                val out: ByteArray = auth.cipher.doFinal(password.toString().toByteArray(Charsets.UTF_8))
                credentialsStore.save(
                    ciphertext = Base64.encodeToString(out, Base64.NO_WRAP),
                    iv = Base64.encodeToString(auth.cipher.iv, Base64.NO_WRAP),
                )
                BiometricResult.Success
            }
            is PromptOutcome.Failure -> auth.result
        }
    }

    override suspend fun decryptStoredPassword(
        title: String,
        subtitle: String,
        negativeButton: String,
        biometricPlatformWrapper: BiometricPlatformWrapper,
    ): DecryptedPasswordResult {
        val (ciphertextStr, ivStr) = credentialsStore.load()
            ?: return DecryptedPasswordResult.Unavailable
        val activity: FragmentActivity = biometricPlatformWrapper.activity
        val ciphertext: ByteArray = Base64.decode(ciphertextStr, Base64.NO_WRAP)
        val iv: ByteArray = Base64.decode(ivStr, Base64.NO_WRAP)
        val secretKey: SecretKey = try {
            existingKey() ?: run {
                clearStoredPassword()
                return DecryptedPasswordResult.Unavailable
            }
        } catch (t: KeyPermanentlyInvalidatedException) {
            logger.e(t) { "Key permanently invalidated" }
            clearStoredPassword()
            return DecryptedPasswordResult.Unavailable
        } catch (t: Throwable) {
            logger.e(t) { "Keystore failure" }
            return DecryptedPasswordResult.Failure(t.message ?: "Keystore failure")
        }
        val cipher: Cipher = try {
            Cipher.getInstance(TRANSFORMATION).apply {
                init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_BITS, iv))
            }
        } catch (t: KeyPermanentlyInvalidatedException) {
            logger.e(t) { "Key permanently invalidated" }
            clearStoredPassword()
            return DecryptedPasswordResult.Unavailable
        } catch (t: Throwable) {
            logger.e(t) { "Cipher init failed" }
            return DecryptedPasswordResult.Failure(t.message ?: "Cipher init failed")
        }
        return when (val auth: PromptOutcome = runPrompt(activity, cipher, title, subtitle, negativeButton)) {
            is PromptOutcome.Authenticated -> {
                val plain: ByteArray = auth.cipher.doFinal(ciphertext)
                DecryptedPasswordResult.Success(plain.toString(Charsets.UTF_8))
            }
            is PromptOutcome.Failure -> when (auth.result) {
                BiometricResult.Cancelled -> DecryptedPasswordResult.Cancelled
                BiometricResult.Unavailable -> DecryptedPasswordResult.Unavailable
                is BiometricResult.Error -> DecryptedPasswordResult.Failure(auth.result.message)
                else -> DecryptedPasswordResult.Failure(auth.result.toString())
            }
        }
    }

    override suspend fun clearStoredPassword() {
        credentialsStore.clear()
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
        val builder = KeyGenParameterSpec
            .Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(true)
        // setInvalidatedByBiometricEnrollment requires API 24; minSdk is 23.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setInvalidatedByBiometricEnrollment(true)
        }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        generator.init(builder.build())
        return generator.generateKey()
    }

    // BiometricPrompt.authenticate(...) must run on the main thread; ViewModels invoke us from
    // Dispatchers.IO, so we hop to Main.immediate before showing the prompt.
    private suspend fun runPrompt(
        activity: FragmentActivity,
        cipher: Cipher,
        title: String,
        subtitle: String,
        negativeButton: String,
    ): PromptOutcome = withContext(Dispatchers.Main.immediate) {
        suspendCancellableCoroutine { continuation ->
            val executor = ContextCompat.getMainExecutor(appContext)
            val callback = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    val resultCipher: Cipher? = result.cryptoObject?.cipher
                    val outcome: PromptOutcome = if (resultCipher == null) {
                        PromptOutcome.Failure(BiometricResult.Error("Missing CryptoObject"))
                    } else {
                        PromptOutcome.Authenticated(resultCipher)
                    }
                    if (continuation.isActive) continuation.resume(outcome)
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    val mapped: BiometricResult = when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                        BiometricPrompt.ERROR_CANCELED -> BiometricResult.Cancelled
                        BiometricPrompt.ERROR_NO_BIOMETRICS,
                        BiometricPrompt.ERROR_HW_NOT_PRESENT,
                        BiometricPrompt.ERROR_HW_UNAVAILABLE -> BiometricResult.Unavailable
                        else -> BiometricResult.Error(errString.toString())
                    }
                    if (continuation.isActive) continuation.resume(PromptOutcome.Failure(mapped))
                }
                override fun onAuthenticationFailed() {
                    // Wrong fingerprint; the system gives the user another try, so we wait for
                    // the terminal onAuthenticationError callback before resuming.
                }
            }
            val prompt = BiometricPrompt(activity, executor, callback)
            val info = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setNegativeButtonText(negativeButton)
                .setAllowedAuthenticators(BIOMETRIC_STRONG)
                .build()
            continuation.invokeOnCancellation { runCatching { prompt.cancelAuthentication() } }
            prompt.authenticate(info, BiometricPrompt.CryptoObject(cipher))
        }
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
    }
}
