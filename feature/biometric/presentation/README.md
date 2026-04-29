# feature:biometric:presentation

Biometric enroll ViewModel — handles password verification and biometric key enrollment.

## Overview

Contains `BiometricEnrollViewModel` and its state/action types. The ViewModel is invoked from the `BiometricEnrollDialog` composable in `core:ui`.

## Components

### `BiometricEnrollViewModel`

MVI ViewModel following the Action Interface Pattern:

```kotlin
class BiometricEnrollViewModel(
    checkPasswordUseCase: CheckPasswordUseCase,
    biometricInteractor: BiometricInteractor,
    snackbarInteractor: SnackbarInteractor,
    router: Router,
    coroutineDispatchers: CoroutineDispatchers,
) : ViewModel()
```

Flow:
1. User types the current database password.
2. `OnEnrollClick` — validates via `CheckPasswordUseCase`.
3. On correct password → calls `BiometricInteractor.encryptAndStorePassword()` — shows the system biometric prompt.
4. On `BiometricResult.Success` → `router.popBackStack()`.
5. On any other result → shows a snackbar with the error.

### `BiometricEnrollResult`

```kotlin
data class BiometricEnrollResult(
    val loading: Boolean,
    val fieldLabel: FieldLabel,
    val password: String,
    val isPasswordVisible: Boolean,
    val isError: Boolean,
)
```

### `BiometricEnrollAction`

```kotlin
sealed interface BiometricEnrollAction {
    data object Cancel
    data class OnEditPassword(val password: String)
    data object TogglePasswordVisibility
    data class OnEnrollClick(val title: String, val subtitle: String, val negativeButton: String)
}
```

## Dependencies

- `feature:biometric:domain` — `BiometricInteractor`, `BiometricResult`
- `core:domain` — `CheckPasswordUseCase`, `CoroutineDispatchers`, `CountingIdlingRes`
- `core:presentation` — `SnackbarInteractor`, `Router`, `FieldLabel`
- `androidx.lifecycle:lifecycle-viewmodel` (multiplatform)
- `kermit` logging

## Testing

```bash
./gradlew :feature:biometric:presentation:androidHostTest
```
