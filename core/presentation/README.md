# Core Presentation Module

## Overview

The `core:presentation` module implements the **presentation layer** of the application using **MVVM (Model-View-ViewModel)** architecture pattern. It contains ViewModels that manage UI state and coordinate business logic through use cases, providing a clean separation between business logic and UI.

## Purpose

- Implement ViewModels for screen state management
- Define UI state models and sealed classes for results
- Handle navigation logic through Router abstraction
- Manage user interactions and coordinate use cases
- Provide reactive data streams for UI consumption

## Architecture

```
core:presentation (Presentation Layer - Platform-independent ViewModels)
    ├── presentation/
    │   ├── main/           # Notes list screen
    │   ├── note/           # Note detail/edit screen
    │   ├── splash/         # App startup screen
    │   ├── signin/         # Database password entry
    │   ├── title/          # Note title editing
    │   └── settings/       # App settings
    │       └── security/   # Password management
    │           ├── enter/  # Enter password
    │           ├── confirm/# Confirm password
    │           └── change/ # Change password
    ├── interactor/         # Presentation-facing interactors (Adaptive, Snackbar)
    └── navigation/
        ├── AppNavGraph.kt  # Navigation graph definition
        └── Router.kt       # Navigation abstraction
```

## Key Components

### ViewModels (`presentation/`)

Each screen has a dedicated ViewModel following **MVVM pattern**:

#### Main Screen
- `MainViewModel`: Manages the note list, keeps the encrypted database alive, and coordinates `AdaptiveInteractor` so adaptive layouts stay in sync.
- `NoteListResult`: Sealed interface with `Loading`, `Success(result: Flow<PagingData<Note>>, selectedId: Long?)`, and `Error` variants.

#### Note Screen
- `NoteViewModel`: Manages note editing state and content
- `SaveViewModel`: Handles note saving logic
- `DeleteViewModel`: Handles note deletion with confirmation
- `NoteResult`: Sealed class for note loading states

#### Authentication/Security
- `SplashViewModel`: Initial app state and database initialization
- `SignInViewModel`: Database password entry
- `SignInResult`: Authentication states

#### Settings
- `SettingsViewModel`: App settings management
- `SecurityResult`: Security settings states
- **Password Management:**
  - `EnterViewModel`: Enter new password
  - `ConfirmViewModel`: Confirm password
  - `ChangeViewModel`: Change existing password

#### Title Editing
- `EditTitleViewModel`: Note title editing
- `EditTitleResult`: Title editing states

### Interactors (`interactor/`)

- `AdaptiveInteractor`: Shared state holder that bridges adaptive navigation between `MainViewModel`, router, and UI panes.
- `SnackbarInteractor`: Multiplatform contract used by ViewModels to emit UI messages without depending on Compose APIs.

### Navigation (`navigation/`)

- `AppNavGraph`: Type-safe navigation destinations using sealed classes
- `Router`: Navigation abstraction that sets controllers, propagates adaptive navigator hooks, and exposes generic navigation methods used across ViewModels.

## Design Patterns

1. **MVVM (Model-View-ViewModel)**: Clear separation of concerns
2. **Action Interface Pattern**: Single `onAction()` method for event handling (reduces callback hell)
3. **Unidirectional Data Flow**: State flows from ViewModel to UI
4. **Sealed Interfaces for Actions**: Type-safe action representation
5. **Data Classes for State**: Immutable state with helper methods
6. **Single State Exposure**: ViewModels expose `StateFlow<Result>` for UI state
7. **Dependency Injection**: ViewModels receive dependencies through constructor
8. **Repository Pattern**: ViewModels use repositories, not DAOs directly
9. **Use Case Pattern**: Business logic delegated to domain use cases

## State Management

- Every ViewModel exposes immutable `StateFlow` (for example, `MainViewModel.stateFlow`).
- Internal state is kept in private `MutableStateFlow` instances and updated from `viewModelScope` using `CoroutineDispatchers` for threading.
- Long-lived collectors (like adaptive selection synchronization) keep a `Job` reference to avoid leaking multiple collectors.

### Result Classes

Result types live next to their ViewModels (`*Result.kt`) and follow these patterns:

- `NoteListResult` tracks loading/success/error and the currently selected note ID for adaptive layouts.
- `NoteResult` represents the editor state, including validation errors and password prompts.
- `SecurityResult`, `EnterResult`, `ConfirmResult`, and `ChangeResult` model password flows with explicit one-shot events.

When extending behaviour, prefer adding new sealed interface branches instead of ad-hoc booleans in the state.

## Multiplatform Support

This module is **Kotlin Multiplatform** and supports:

- ✅ **Android** (androidTarget)
- ✅ **iOS** (iosArm64, iosSimulatorArm64)
- ✅ **Desktop JVM** (jvm)
- ✅ **Web** (wasmJs)

All ViewModels are **100% shared** across platforms with no platform-specific code.

## Dependencies

### Core Dependencies
- `core:domain` - Domain models and use cases
- `androidx-lifecycle-viewmodel` - ViewModel base class (multiplatform)
- `kotlinx-serialization-json` - Serialization support
- `kotlinx-coroutines` - Asynchronous programming
- `androidx-paging-common` - Pagination support
- `kermit` - Logging

### Testing Dependencies
- `core:test` - Test utilities
- `kotlinx-coroutines-test` - Coroutine testing
- `turbine` - Flow testing
- `mockito` (Android) - Mocking framework
- `androidx-arch-core-testing` (Android) - LiveData/ViewModel testing

## Testing Strategy

### Unit Tests (`androidUnitTest/`)

- `MainViewModelTest`, `NoteViewModelTest`, and the security test suite validate action routing, paging integration, and error handling. Use `MainDispatcherRule` and fakes for repositories/interactors to keep tests deterministic.
- Password flows (`EnterViewModelTest`, `ConfirmViewModelTest`, `ChangeViewModelTest`) ensure dialog ViewModels stay in sync with encryption use cases.
- When adding a new ViewModel, follow the existing pattern: configure coroutine rules, provide fake `Router`/`SnackbarInteractor`, drive `onAction`, and assert on the exposed `StateFlow`.

### Running Tests

```bash
./gradlew :core:presentation:test             # Multiplatform tests
./gradlew :core:presentation:testDebugUnitTest # Android unit tests
```

## ViewModel Lifecycle

ViewModels follow Android Architecture Components lifecycle:

1. **Creation**: Constructed with dependencies via Koin DI
2. **Active**: Manages state while screen is active
3. **Cleared**: `onCleared()` called when no longer needed
4. **Cleanup**: Coroutines in `viewModelScope` automatically cancelled

## Navigation Abstraction

The `Router` interface (see `navigation/Router.kt`) keeps ViewModels platform-agnostic:

- `setController` / `releaseController` store the platform navigation controller.
- Generic `navigate`, `navigateClearingBackStack`, and `popBackStack` accept strongly typed destinations (`AppNavGraph`).
- Adaptive helpers (`setAdaptiveNavigator`, `adaptiveNavigateToDetail`, `adaptiveNavigateBack`) coordinate multi-pane navigation via `AdaptiveInteractor`.
- `RouterImpl` in the UI layer backs these calls with Jetpack Navigation and compose-adaptive primitives.

## Error Handling

- Each ViewModel centralizes failures in a `handleError` helper that logs with Kermit and emits the proper `Result` state.
- Database failures send the user back to `AppNavGraph.Splash` using `Router.navigateClearingBackStack`.
- Surface user-facing errors via `SnackbarInteractor` rather than duplicating dialog logic.

## AI Agent Guidelines

**See [CONTRIBUTING.md](../CONTRIBUTING.md) for general guidelines.**

### Presentation-Specific
- Use `StateFlow` for UI state, not `LiveData`
- Define state as sealed classes for type safety
- One `StateFlow` per screen
- No UI logic in ViewModels
- All async operations in `viewModelScope`
- Use Router for navigation, not direct calls
- Interact with UI toasts/snackbars exclusively through `SnackbarInteractor`
- Keep adaptive navigation in sync by writing selected IDs to `AdaptiveInteractor`

## Best Practices

### ViewModel Structure with Action Interface Pattern

- Define a `sealed interface <Screen>Action` alongside the result/state file.
- Expose a single `onAction(action: <Screen>Action)` entry point; branch internally to small, private helpers.
- Apply the pattern on screens with three or more UI callbacks (Main, Note, Settings, password flows).
- Keep lightweight dialogs simple—`SignInViewModel`, `SaveViewModel`, and `DeleteViewModel` call methods directly instead of introducing actions.
- Always provide a `disposeOneTimeEvents()` (or similar) when the UI needs to clear transient signals after handling them.

**Benefits**: fewer composable parameters, type-safe events, easier snapshot testing, and centralized navigation/error handling.

## Related Modules

- **Used by**: `ui:shared`, `app:android`, `app:desktop`, `app:web`, `app:ios-kit`
- **Depends on**: `core:domain`
- **Test dependencies**: `core:test`

