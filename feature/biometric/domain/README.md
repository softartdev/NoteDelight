# feature:biometric:domain

Biometric authentication domain module — platform-agnostic contract plus platform-specific implementations.

## Overview

Provides the `BiometricInteractor` expect class and its platform actuals, as well as `BiometricResult` / `DecryptedPasswordResult` domain types, and the `ActivityProvider` expect class used to pass the Android host Activity to the biometric prompt from Compose.

## API

### `BiometricInteractor`

```kotlin
expect class BiometricInteractor {
    suspend fun canAuthenticate(): Boolean
    suspend fun hasStoredPassword(): Boolean
    suspend fun encryptAndStorePassword(
        password: CharSequence,
        title: String,
        subtitle: String,
        negativeButton: String,
        activityProvider: ActivityProvider,
    ): BiometricResult
    suspend fun decryptStoredPassword(
        title: String,
        subtitle: String,
        negativeButton: String,
        activityProvider: ActivityProvider,
    ): DecryptedPasswordResult
    suspend fun clearStoredPassword()
}
```

The `activityProvider` parameter is created in `@Composable` functions via `rememberActivityProvider()` (defined in `core:ui`) and stored as a var property on the ViewModel. On Android it carries the current `FragmentActivity`; on all other platforms it is an empty stub.

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
    data object Cancelled : DecryptedPasswordResult    // user dismissed the prompt
    data object Unavailable : DecryptedPasswordResult  // no stored credential / key invalidated
    data class Failure(val message: String) : DecryptedPasswordResult // unrecoverable error
}
```

`Cancelled` and `Unavailable` are modelled as separate objects so callers can distinguish intent (user cancelled voluntarily vs. hardware/key no longer valid) without nesting `BiometricResult` inside `DecryptedPasswordResult`.

### `ActivityProvider`

```kotlin
expect class ActivityProvider
// Android actual:  actual class ActivityProvider(val activity: FragmentActivity)
// iOS/JVM/wasmJs: actual class ActivityProvider   (empty stub)
```

Created from a Composable using `rememberActivityProvider()` (in `core:ui`) and stored as a var property on the ViewModel. Passed to `encryptAndStorePassword` / `decryptStoredPassword` so that the Android implementation can instantiate `BiometricPrompt`.

## Platform Implementations

### Android (`androidMain`)

**Password storage** uses two independent layers of protection:

1. **Android Keystore (AES-256-GCM)** — A hardware-backed symmetric key (`notedelight_biometric_key`) is generated with `setUserAuthenticationRequired(true)` and (on API 24+) `setInvalidatedByBiometricEnrollment(true)`. The key can only be used after a successful biometric authentication and never leaves the secure hardware element.

2. **DataStore Preferences** (`BiometricCredentialsStore`)— The *encrypted* output of AES-GCM (ciphertext + IV, both Base64-encoded) is stored via DataStore Preferences.

> **Is the DataStore encrypted?** No — DataStore writes a plain binary Protobuf file on disk and applies no application-level encryption. However, the *values* stored inside it are already opaque AES-GCM ciphertext — they cannot be decrypted without the Android Keystore key, which is hardware-bound and biometric-gated and never leaves the secure element. An attacker with raw filesystem access would obtain unintelligible bytes with no way to recover the plaintext password without also defeating the device's secure hardware.

**Enroll flow** (`encryptAndStorePassword`):
1. The existing Keystore key is reused, or a new one is generated.
2. A `Cipher` is initialised in `ENCRYPT_MODE` with the Keystore key.
3. `BiometricPrompt` shows the system biometric UI (via `runPrompt`); the `Cipher` is passed as a `CryptoObject` so Android can attest the authentication.
4. On success the `CryptoObject`'s cipher is used to encrypt the password bytes (AES-256-GCM).
5. `BiometricCredentialsStore.save(ciphertext, iv)` persists both Base64-encoded values.

**Sign-in flow** (`decryptStoredPassword`):
1. `BiometricCredentialsStore.load()` retrieves the stored `(ciphertext, iv)` pair; returns `Unavailable` if absent.
2. The Keystore key is looked up; if absent or permanently invalidated (`KeyPermanentlyInvalidatedException`), the stored credential is cleared and `Unavailable` is returned.
3. A `Cipher` is initialised in `DECRYPT_MODE` with the key + stored IV via `GCMParameterSpec`.
4. `BiometricPrompt` shows the biometric UI; on success the plaintext password is decrypted and returned as `DecryptedPasswordResult.Success`.
5. User cancels → `Cancelled`; hardware unavailable / key gone → `Unavailable`; other error → `Failure(message)`.

**Key design decisions**:
- `BiometricPrompt.authenticate()` must run on the main thread — `runPrompt()` uses `withContext(Dispatchers.Main.immediate)`.
- `ActivityProvider` is supplied from the composable layer (`rememberActivityProvider()` in `core:ui` uses `LocalContext.current as FragmentActivity`), stored as a var property on the ViewModel (same pattern as `autofillManager`). This replaces the previous `CurrentActivityProvider` (an `ActivityLifecycleCallbacks` singleton) which was broken because no lifecycle callbacks fired after the Koin singleton was created at app startup.
- `setInvalidatedByBiometricEnrollment(true)` is wrapped in `Build.VERSION.SDK_INT >= Build.VERSION_CODES.N` (API 24 lint requirement; minSdk is 23).

**`BiometricCredentialsStore`** (internal, Android-only):
Encapsulates all DataStore read/write operations. Exposes `hasCredentials()`, `load(): Pair<String,String>?`, `save(ciphertext, iv)`, `clear()`.

### iOS (`iosMain`)

**Password storage** uses a single layer: the iOS **Keychain** with `kSecAccessControlBiometryCurrentSet`.

1. `encryptAndStorePassword`:
   - `LAContext.evaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics)` is called first to obtain explicit user consent.
   - On success, the **plaintext** password bytes are stored directly in a Keychain item protected by `kSecAccessControlBiometryCurrentSet`.
   - The access control flag means the item is automatically invalidated if the device's biometric enrollment changes (new finger/face added or removed).

2. `decryptStoredPassword`:
   - An `LAContext` is configured with the prompt strings and passed as `kSecUseAuthenticationContext` in the `SecItemCopyMatching` query.
   - The Keychain enforces biometric authentication implicitly when reading the protected item.
   - On success the raw `NSData` is decoded as UTF-8 and returned as `DecryptedPasswordResult.Success`.
   - `errSecItemNotFound` → `Unavailable` (item gone or biometry enrollment changed); other statuses are mapped via `mapKeychainStatus`.

> **Security note**: On iOS the password is **not** encrypted at the application layer — it is stored as plaintext bytes inside a hardware-protected Keychain item. Security is provided entirely by the Secure Enclave and `kSecAccessControlBiometryCurrentSet`. The flag ensures the item is bound to the current biometric set and invalidated on any enrollment change.

### Desktop/Web (`jvmMain`, `wasmJsMain`)

Stub actuals — `canAuthenticate()` returns `false`, all operations return `Unavailable` or `BiometricResult.Unavailable`. Biometric is not supported on Desktop or Web.

## Dependencies

- `kotlinx-coroutines-core` (common)
- `kermit` logging (common)
- `androidx.biometric:biometric:1.1.0` (Android only)
- `androidx.appcompat:appcompat` (Android only — for `FragmentActivity`)
- `androidx.datastore:datastore-preferences` (Android only — `BiometricCredentialsStore`)
