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
    └── navigation/
        ├── AppNavGraph.kt  # Navigation graph definition
        └── Router.kt       # Navigation abstraction
```

## Key Components

### ViewModels (`presentation/`)

Each screen has a dedicated ViewModel following **MVVM pattern**:

#### Main Screen
- `MainViewModel`: Manages note list with pagination, handles database connection
- `NoteListResult`: Sealed class representing list states (Loading, Success, Error)

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

### Navigation (`navigation/`)

- `AppNavGraph`: Type-safe navigation destinations using sealed classes
- `Router`: Navigation abstraction decoupling ViewModels from platform-specific navigation

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

ViewModels use **Kotlin Flow** and **StateFlow** for reactive state management:

```kotlin
class MainViewModel(
    private val safeRepo: SafeRepo,
    private val router: Router,
    private val coroutineDispatchers: CoroutineDispatchers,
) : ViewModel() {
    private val mutableStateFlow: MutableStateFlow<NoteListResult> = MutableStateFlow(
        value = NoteListResult.Loading
    )
    val stateFlow: StateFlow<NoteListResult> = mutableStateFlow
    
    // UI observes stateFlow for updates
}
```

### Result Classes

Sealed classes represent UI states:

```kotlin
sealed class NoteListResult {
    data object Loading : NoteListResult()
    data class Success(val result: Flow<PagingData<Note>>) : NoteListResult()
    data class Error(val message: String?) : NoteListResult()
}
```

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
- `napier` - Logging

### Testing Dependencies
- `core:test` - Test utilities
- `kotlinx-coroutines-test` - Coroutine testing
- `turbine` - Flow testing
- `mockito` (Android) - Mocking framework
- `androidx-arch-core-testing` (Android) - LiveData/ViewModel testing

## Testing Strategy

### Unit Tests (`androidUnitTest/`)

All ViewModels have comprehensive unit tests:

```kotlin
class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    @Test
    fun testNotesList() = runTest {
        // Test ViewModel logic
    }
}
```

### Test Coverage

- ✅ `MainViewModelTest` - Note list loading and pagination
- ✅ `NoteViewModelTest` - Note editing and saving
- ✅ `DeleteViewModelTest` - Note deletion flow
- ✅ `SaveViewModelTest` - Note save operations
- ✅ `SignInViewModelTest` - Authentication flow
- ✅ `SplashViewModelTest` - App initialization
- ✅ `SettingsViewModelTest` - Settings management
- ✅ `EnterViewModelTest`, `ConfirmViewModelTest`, `ChangeViewModelTest` - Password flows

### Running Tests

```bash
# Run all presentation tests
./gradlew :core:presentation:test

# Run Android unit tests specifically
./gradlew :core:presentation:testDebugUnitTest
```

## ViewModel Lifecycle

ViewModels follow Android Architecture Components lifecycle:

1. **Creation**: Constructed with dependencies via Koin DI
2. **Active**: Manages state while screen is active
3. **Cleared**: `onCleared()` called when no longer needed
4. **Cleanup**: Coroutines in `viewModelScope` automatically cancelled

## Navigation Abstraction

The `Router` interface decouples ViewModels from platform-specific navigation:

```kotlin
interface Router {
    fun navigate(route: AppNavGraph)
    fun navigateClearingBackStack(route: AppNavGraph)
    fun popBackStack()
}
```

ViewModels use Router for navigation:

```kotlin
fun onNoteClicked(id: Long) = router.navigate(route = AppNavGraph.Details(noteId = id))
```

Platform-specific UI layers implement Router using their navigation frameworks (Jetpack Navigation, etc.).

## Error Handling

ViewModels handle errors gracefully and expose them through state:

```kotlin
private fun handleError(throwable: Throwable) {
    Napier.e("❌", throwable)
    if (isDbError(throwable)) {
        router.navigateClearingBackStack(AppNavGraph.Splash)
    }
    mutableStateFlow.value = NoteListResult.Error(throwable.message)
}
```

## AI Agent Guidelines

**See [CONTRIBUTING.md](../CONTRIBUTING.md) for general guidelines.**

### Presentation-Specific
- Use `StateFlow` for UI state, not `LiveData`
- Define state as sealed classes for type safety
- One `StateFlow` per screen
- No UI logic in ViewModels
- All async operations in `viewModelScope`
- Use Router for navigation, not direct calls

## Best Practices

### ViewModel Structure with Action Interface Pattern

**All ViewModels follow this pattern** to reduce callback hell and simplify Composable signatures:

```kotlin
// 1. Define Actions (in *Result.kt file)
sealed interface ScreenAction {
    data class LoadData(val id: Long) : ScreenAction
    data class Save(val title: String) : ScreenAction
    data object Delete : ScreenAction
}

// 2. Define State (in *Result.kt file)
data class ScreenResult(
    val loading: Boolean = false,
    val data: DataType? = null,
    val error: String? = null,
) {
    fun showLoading(): ScreenResult = copy(loading = true)
    fun hideLoading(): ScreenResult = copy(loading = false)
}

// 3. ViewModel with single onAction() dispatcher
class ScreenViewModel(
    private val useCase: SomeUseCase,
    private val router: Router,
    private val dispatchers: CoroutineDispatchers,
) : ViewModel() {
    
    // Private mutable state
    private val mutableStateFlow = MutableStateFlow(ScreenResult())
    
    // Public immutable state
    val stateFlow: StateFlow<ScreenResult> = mutableStateFlow
    
    // Single action dispatcher
    fun onAction(action: ScreenAction) = when (action) {
        is ScreenAction.LoadData -> loadData(action.id)
        is ScreenAction.Save -> saveData(action.title)
        is ScreenAction.Delete -> deleteData()
    }
    
    // Private implementation methods
    private fun loadData(id: Long) {
        viewModelScope.launch(dispatchers.io) {
            try {
                val data = useCase.load(id)
                mutableStateFlow.value = ScreenResult(data = data)
            } catch (e: Throwable) {
                handleError(e)
            }
        }
    }
    
    private fun saveData(title: String) { /* ... */ }
    private fun deleteData() { /* ... */ }
}
```

**Benefits**:
- ✅ Single `onAction()` parameter in Composables (instead of 5+ callbacks)
- ✅ Type-safe actions with sealed interfaces
- ✅ Centralized event handling
- ✅ Easier testing (mock one method, not many)
- ✅ Clear separation between public API and private implementation

**When to Use**:
- ✅ Complex screens with **3+ actions** passed through Composables
- ❌ Simple 1-2 action ViewModels (call methods directly)
- ❌ Methods called where ViewModel is obtained (e.g., `disposeOneTimeEvents()`)

**ViewModels Using Action Pattern**:
- `NoteViewModel` (4 actions), `MainViewModel` (3 actions), `SettingsViewModel` (7 actions)
- `EditTitleViewModel` (3 actions), `EnterViewModel` (4 actions)
- `ChangeViewModel` (5 actions), `ConfirmViewModel` (4 actions)

**ViewModels NOT Using Actions** (kept simple):
- `SignInViewModel` - Single `signIn()` method
- `SaveViewModel`, `DeleteViewModel` - Simple dialogs
- All ViewModels keep `disposeOneTimeEvents()` as public method (called directly)

## Related Modules

- **Used by**: `ui:shared`, `app:android`, `app:desktop`, `app:web`, `app:ios-kit`
- **Depends on**: `core:domain`
- **Test dependencies**: `core:test`

