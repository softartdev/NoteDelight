# feature:biometric:domain

Biometric authentication domain module — platform-agnostic contract plus platform-specific implementations.

## Overview

Provides the `BiometricInteractor` expect class and its platform actuals, as well as `BiometricResult` / `DecryptedPasswordResult` domain types.

## API

### `BiometricInteractor`

```kotlin
expect class BiometricInteractor {
    suspend fun canAuthenticate(): Boolean
    fun hasStoredPassword(): Boolean
    suspend fun encryptAndStorePassword(password, title, subtitle, negativeButton): BiometricResult
    suspend fun decryptStoredPassword(title, subtitle, negativeButton): DecryptedPasswordResult
    fun clearStoredPassword()
}
```

### `BiometricResult`

```kotlin
sealed interface BiometricResult {
    data object Success : BiometricResult
    data object Failed : BiometricResult
    data object Cancelled : BiometricResult
    data object Unavailable : BiometricResult
    data class Error(val message: String) : BiometricResult
}
```

### `DecryptedPasswordResult`

```kotlin
sealed interface DecryptedPasswordResult {
    data class Success(val password: CharSequence) : DecryptedPasswordResult
    data class Failure(val result: BiometricResult) : DecryptedPasswordResult
}
```

## Platform Implementations

### Android (`androidMain`)

- `BiometricInteractor.android.kt`: Wraps `androidx.biometric.BiometricPrompt` with AES-GCM/AndroidKeyStore encryption. The encrypted `{ciphertext, iv}` pair is stored in `SharedPreferences`; the AES key is hardware-bound and requires biometric authentication to use.
- `CurrentActivityProvider.kt`: Implements `Application.ActivityLifecycleCallbacks` to expose the current `FragmentActivity` to `BiometricPrompt` without coupling to a specific Activity instance.

Key design decisions:
- `BiometricPrompt.authenticate()` must run on the main thread — `runPrompt()` uses `withContext(Dispatchers.Main.immediate)`.
- `CurrentActivityProvider` sets the ref on `onActivityCreated`, `onActivityStarted`, and `onActivityResumed`; clears only on `onActivityDestroyed` — deliberate: brief pause (overlay, config change) must not blank the host.
- `setInvalidatedByBiometricEnrollment(true)` wrapped in `Build.VERSION.SDK_INT >= Build.VERSION_CODES.N` (API 24 lint).

### iOS (`iosMain`)

Uses `LocalAuthentication.LAContext.evaluatePolicy` + Keychain with `kSecAccessControlBiometryCurrentSet` to bind the stored password to the current biometric set.

### Desktop/Web (`jvmMain`, `wasmJsMain`)

Stub actuals — `canAuthenticate()` returns `false`, all operations return `Unavailable`. Biometric is not supported on Desktop or Web.

## Dependencies

- `kotlinx-coroutines-core` (common)
- `kermit` logging (common)
- `androidx.biometric:biometric:1.1.0` (Android only)
- `androidx.appcompat:appcompat` (Android only — for `FragmentActivity`)
