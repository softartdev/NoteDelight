# Architecture Documentation

## Overview

NoteDelight follows **Clean Architecture** principles combined with **MVVM (Model-View-ViewModel)** pattern, built entirely with **Kotlin Multiplatform** for maximum code sharing across platforms.

## Architecture Layers

### Visual Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                      APP MODULES                                │
│  (Platform-Specific Entry Points)                              │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐          │
│  │ Android  │ │ Desktop  │ │   iOS    │ │   Web    │          │
│  └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘          │
└───────┼────────────┼────────────┼────────────┼─────────────────┘
        │            │            │            │
        └────────────┴────────────┴────────────┘
                     │
┌────────────────────▼────────────────────────────────────────────┐
│                      UI LAYER                                   │
│  (Compose Multiplatform - 100% Shared)                         │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  Screens, Components, Navigation, Theme                 │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────┬───────────────────────────────────────┘
                          │ observes
┌─────────────────────────▼───────────────────────────────────────┐
│                  PRESENTATION LAYER                             │
│  (ViewModels - MVVM Pattern)                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  State Management, Business Logic Coordination          │   │
│  │  StateFlow<State>, Event Handling, Navigation           │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────┬───────────────────────────────────────┘
                          │ uses
┌─────────────────────────▼───────────────────────────────────────┐
│                     DOMAIN LAYER                                │
│  (Business Logic - Pure Kotlin)                                │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────┐  │
│  │   Use Cases      │  │     Entities     │  │ Repository   │  │
│  │  (Operations)    │  │     (Models)     │  │  Interfaces  │  │
│  └──────────────────┘  └──────────────────┘  └──────────────┘  │
└─────────────────────────────────────────┬───────────────────────┘
                                          │ implements
┌───────────────────────────────────────▼─────────────────────────┐
│                      DATA LAYER                                 │
│  (Data Access & Persistence)                                   │
│  ┌──────────────────┐              ┌──────────────────┐        │
│  │   SQLDelight     │      OR      │      Room        │        │
│  │  (Default Impl)  │              │ (Alternative)    │        │
│  └──────────────────┘              └──────────────────┘        │
│  DAOs, Database, Repositories, Data Sources                    │
└─────────────────────────────────────────────────────────────────┘
```

## Core Principles

### 1. Separation of Concerns

Each layer has a clear responsibility:

- **Domain**: Business rules and logic (platform-independent)
- **Presentation**: UI state management and user interaction
- **Data**: Data access and persistence
- **UI**: User interface rendering

### 2. Dependency Rule

**Dependencies point inward** - outer layers depend on inner layers, never the reverse:

```
UI → Presentation → Domain ← Data
```

- **Domain** has zero dependencies (pure Kotlin)
- **Presentation** depends only on Domain
- **Data** implements Domain interfaces
- **UI** depends on Presentation (and Domain for models)

### 3. Abstraction

- Domain layer defines **interfaces** (e.g., `NoteDAO`, `DatabaseHolder`)
- Data layer provides **implementations** (e.g., `NoteSQLDelightDAO`)
- Presentation layer depends on **abstractions**, not implementations

## Layer Details

### Domain Layer

**Location**: `core:domain`

**Purpose**: Business logic, entities, and abstractions

**Key Components**:
- **Entities** (`model/`): Business objects (Note, PlatformSQLiteState)
- **Use Cases** (`usecase/`): Single-purpose business operations
- **Repository Interfaces** (`db/`): Data access contracts
- **Utilities** (`util/`): Platform-agnostic helpers

**Rules**:
- ✅ Pure Kotlin (no platform dependencies)
- ✅ Immutable data classes
- ✅ No framework dependencies
- ❌ No UI code
- ❌ No data implementation details

**Example**:
```kotlin
// Entity
data class Note(
    val id: Long,
    val title: String,
    val text: String,
    val dateCreated: LocalDateTime,
    val dateModified: LocalDateTime
)

// Use Case
class CreateNoteUseCase(private val noteDAO: NoteDAO) {
    suspend operator fun invoke(title: String, text: String): Long {
        // Business logic
        val note = Note(...)
        noteDAO.insert(note)
        return note.id
    }
}

// Repository Interface
interface NoteDAO {
    val listFlow: Flow<List<Note>>
    suspend fun insert(note: Note)
    suspend fun delete(note: Note)
}
```

### Presentation Layer

**Location**: `core:presentation`

**Purpose**: Manage UI state and coordinate business logic

**Key Components**:
- **ViewModels** (`presentation/`): Screen state managers
- **State Classes** (`presentation/*/Result.kt`): Data classes for UI state
- **Action Interfaces** (`presentation/*/Result.kt`): Sealed interfaces for user actions
- **Navigation** (`navigation/`): Navigation abstraction (Router)

**Pattern**: MVVM (Model-View-ViewModel) with Action-Based Event Handling

**Rules**:
- ✅ StateFlow for reactive state
- ✅ Action interfaces for event handling
- ✅ ViewModel lifecycle awareness
- ✅ Platform-independent (multiplatform)
- ❌ No direct UI code (Composables)
- ❌ No data implementation details

**Action Interface Pattern**:

The Action interface pattern reduces callback hell and simplifies `@Composable` function signatures by centralizing event handling through a single `onAction()` method:

```kotlin
// State
data class NoteResult(
    val loading: Boolean = false,
    val note: Note? = null,
    val snackBarMessageType: SnackBarMessageType? = null,
) {
    enum class SnackBarMessageType { SAVED, EMPTY, DELETED }
    
    fun showLoading(): NoteResult = copy(loading = true)
    fun hideLoading(): NoteResult = copy(loading = false)
}

// Action Interface
sealed interface NoteAction {
    data class Save(val title: String?, val text: String) : NoteAction
    data object Edit : NoteAction
    data object Delete : NoteAction
    data class CheckSaveChange(val title: String, val text: String) : NoteAction
}

// ViewModel
class NoteViewModel(...) : ViewModel() {
    private val mutableStateFlow = MutableStateFlow(NoteResult())
    val stateFlow: StateFlow<NoteResult> = mutableStateFlow
    
    fun onAction(action: NoteAction) = when (action) {
        is NoteAction.Save -> saveNote(action.title, action.text)
        is NoteAction.Edit -> editTitle()
        is NoteAction.Delete -> subscribeToDeleteNote()
        is NoteAction.CheckSaveChange -> checkSaveChange(action.title, action.text)
    }
    
    private fun saveNote(title: String?, text: String) { /* ... */ }
    private fun editTitle() { /* ... */ }
    // ...
}

// Composable - single onAction parameter instead of multiple callbacks
@Composable
fun NoteDetail(
    noteViewModel: NoteViewModel,
) {
    val result: NoteResult by noteViewModel.stateFlow.collectAsState()
    NoteDetailBody(
        result = result,
        onAction = noteViewModel::onAction,  // Single action handler
    )
}

@Composable
fun NoteDetailBody(
    result: NoteResult,
    onAction: (action: NoteAction) -> Unit = {},  // Simplified signature
) {
    // Usage in UI components
    IconButton(onClick = { onAction(NoteAction.Save(title, text)) })
    IconButton(onClick = { onAction(NoteAction.Edit) })
    IconButton(onClick = { onAction(NoteAction.Delete) })
}
```

**Benefits**:
- ✅ Reduced callback parameters in Composable functions
- ✅ Type-safe event handling with sealed interfaces
- ✅ Centralized action dispatching
- ✅ Easier testing - single onAction() method to mock
- ✅ Avoids callback hell in complex screens
- ✅ Better state management with explicit action types

**When to Use Action Interfaces**:

Use Action interfaces when you have **3+ different user actions** that need to be passed down through multiple Composable layers. Examples:
- ✅ `NoteAction` (Save, Edit, Delete, CheckSaveChange) - 4 actions
- ✅ `MainAction` (OnNoteClick, OnSettingsClick, OnRefresh) - 3 actions  
- ✅ `SettingsAction` (NavBack, ChangeTheme, CheckEncryption, etc.) - 7 actions
- ✅ `ChangeAction` (OnEditOldPassword, OnEditNewPassword, OnEditRepeatPassword, etc.) - 5 actions

**When NOT to Use Action Interfaces**:

Skip Action interfaces for **simple cases with 1-2 actions** or functions called **directly** where ViewModel is obtained:
- ❌ `SignInViewModel.signIn()` - Single action, call directly
- ❌ `SaveViewModel` - 3 methods but simple dialog, not passed down
- ❌ `DeleteViewModel` - 2 methods but simple dialog, not passed down
- ❌ `disposeOneTimeEvents()` - Called in same LaunchedEffect where ViewModel is obtained

**Rule of Thumb**: Use Actions to avoid callback hell in Composables, not to wrap every ViewModel method.

### Data Layer

**Location**: `core:data:db-sqldelight` or `core:data:db-room`

**Purpose**: Data access and persistence

**Key Components**:
- **DAOs**: Data access objects implementing domain interfaces
- **Database**: Database configuration and setup
- **Repositories**: Repository implementations
- **Data Sources**: Local/remote data sources

**Technologies**:
- **SQLDelight** (default): Type-safe SQL, multiplatform
- **Room** (alternative): Android-first ORM, experimental KMP support
- **SQLCipher**: Database encryption (Android, iOS)

**Rules**:
- ✅ Implements domain interfaces
- ✅ Platform-specific implementations (expect/actual)
- ✅ Handles data mapping
- ❌ No business logic
- ❌ No UI concerns

**Example**:
```kotlin
// DAO Implementation
class NoteSQLDelightDAO(
    private val database: NoteDb
) : NoteDAO {
    override val listFlow: Flow<List<Note>> = 
        database.noteQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { it.map { note -> note.toDomainModel() } }
    
    override suspend fun insert(note: Note) {
        database.noteQueries.insert(note.toDbModel())
    }
}
```

### UI Layer

**Location**: `ui:shared`

**Purpose**: User interface rendering and interaction

**Key Components**:
- **Screens** (`ui/`): Full-screen composables
- **Components** (`ui/component/`): Reusable UI components
- **Theme** (`ui/theme/`): Material 3 theming
- **Navigation**: Navigation graph implementation

**Technology**: Compose Multiplatform

**Rules**:
- ✅ 100% shared across platforms
- ✅ Observes ViewModel state
- ✅ Stateless when possible
- ❌ No business logic
- ❌ No direct data access

**Example**:
```kotlin
@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsState()
    
    when (val currentState = state) {
        is NoteListResult.Loading -> LoadingIndicator()
        is NoteListResult.Success -> NotesList(currentState.notes)
        is NoteListResult.Error -> ErrorMessage(currentState.message)
    }
}
```

## Data Flow

### Unidirectional Data Flow (UDF)

```
┌──────────────────────────────────────────────────────────┐
│                                                          │
│  User Action → ViewModel → Use Case → Repository → DB   │
│                    ↓                                     │
│                  State                                   │
│                    ↓                                     │
│                   UI ←──────────────────────────────────┘
│              (Re-renders)
```

### Example Flow: Create Note

1. **User taps "Create" button** (UI Layer)
2. **UI calls** `viewModel.createNote()`
3. **ViewModel invokes** `CreateNoteUseCase`
4. **Use Case** validates data and calls `noteDAO.insert()`
5. **DAO** persists to database
6. **Database** emits updated data via Flow
7. **ViewModel** updates `StateFlow` with new state
8. **UI** recomposes automatically (observing StateFlow)

## Dependency Injection

### Koin DI

We use **Koin** for dependency injection:

**Benefits**:
- Multiplatform support
- Simple Kotlin DSL
- No code generation
- Easy testing

**Module Organization**:
```kotlin
// Domain module
val domainModule = module {
    factory { CreateNoteUseCase(get()) }
    factory { SaveNoteUseCase(get()) }
    factory { DeleteNoteUseCase(get()) }
}

// Presentation module
val presentationModule = module {
    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { parameters -> NoteViewModel(get(), parameters.get()) }
}

// Data module
val dataModule = module {
    single<NoteDAO> { NoteSQLDelightDAO(get()) }
    single { createDatabase(get()) }
}
```

**Initialization**:
```kotlin
// In app module
startKoin {
    modules(domainModule, presentationModule, dataModule)
}
```

## State Management

### StateFlow Pattern

ViewModels expose immutable `StateFlow<State>`:

```kotlin
class ScreenViewModel : ViewModel() {
    // Private mutable
    private val _stateFlow = MutableStateFlow<State>(State.Initial)
    
    // Public immutable
    val stateFlow: StateFlow<State> = _stateFlow
    
    fun action() {
        viewModelScope.launch {
            _stateFlow.value = State.Loading
            try {
                val result = performOperation()
                _stateFlow.value = State.Success(result)
            } catch (e: Exception) {
                _stateFlow.value = State.Error(e)
            }
        }
    }
}
```

### Sealed Classes for State

Type-safe state representation:

```kotlin
sealed class ScreenState {
    object Initial : ScreenState()
    object Loading : ScreenState()
    data class Success(val data: Data) : ScreenState()
    data class Error(val message: String) : ScreenState()
}
```

### UI Consumption

```kotlin
@Composable
fun Screen(viewModel: ScreenViewModel) {
    val state by viewModel.stateFlow.collectAsState()
    
    when (state) {
        is ScreenState.Initial -> InitialView()
        is ScreenState.Loading -> LoadingView()
        is ScreenState.Success -> ContentView(state.data)
        is ScreenState.Error -> ErrorView(state.message)
    }
}
```

## Navigation

### Router Pattern

**Problem**: ViewModels shouldn't depend on platform-specific navigation

**Solution**: Router abstraction

```kotlin
// Interface (in presentation layer)
interface Router {
    fun <T : Any> navigate(route: T)
    fun <T : Any> navigateClearingBackStack(route: T)
    fun popBackStack(): Boolean
    suspend fun adaptiveNavigateToDetail(contentKey: Long? = null)
    suspend fun adaptiveNavigateBack(): Boolean
}

// Sealed interface for type-safe routes
sealed interface AppNavGraph {
    @Serializable
    data object Main : AppNavGraph
    
    @Serializable
    data class Details(val noteId: Long) : AppNavGraph // managed by adaptive navigation
    
    @Serializable
    data object Settings : AppNavGraph
}

// ViewModel uses Router
class MainViewModel(private val router: Router) : ViewModel() {
    fun onNoteClicked(id: Long) {
        router.navigate(AppNavGraph.Details(id))
    }
}

// UI layer implements Router
class ComposeRouter(private val navController: NavController) : Router {
    override fun navigate(route: AppNavGraph) {
        navController.navigate(route.toRoute())
    }
}
```

## Platform-Specific Code

### Expect/Actual Pattern

For platform-specific implementations:

```kotlin
// Common (expect)
expect class CoroutineDispatchers {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}

// Android (actual)
actual class CoroutineDispatchers {
    actual val main = Dispatchers.Main
    actual val io = Dispatchers.IO
    actual val default = Dispatchers.Default
}

// iOS (actual)
actual class CoroutineDispatchers {
    actual val main = Dispatchers.Main
    actual val io = Dispatchers.Default // No IO dispatcher on iOS
    actual val default = Dispatchers.Default
}
```

## Testing Architecture

**See [TESTING_GUIDE.md](TESTING_GUIDE.md) for comprehensive testing documentation.**

## Multiplatform Strategy

### Code Sharing Levels

1. **100% Shared**:
   - Domain layer (business logic)
   - Presentation layer (ViewModels)
   - UI layer (Compose UI)

2. **Mostly Shared** (with platform specifics):
   - Data layer (database drivers differ)
   - Utilities (platform-specific implementations)

3. **Platform-Specific**:
   - App entry points
   - Platform integration (permissions, etc.)
   - Native features

### Platform Modules

```
commonMain/     # Shared code (100%)
  ├── kotlin/
  └── resources/

androidMain/    # Android-specific
  ├── kotlin/
  └── AndroidManifest.xml

iosMain/        # iOS-specific
  └── kotlin/

jvmMain/        # Desktop JVM-specific
  └── kotlin/

wasmJsMain/     # Web-specific
  └── kotlin/
```

## Best Practices

**See [CONTRIBUTING.md](../CONTRIBUTING.md) for detailed coding guidelines and best practices.**

## Performance Considerations

### Pagination

Use Paging3 library for large datasets:
```kotlin
val pagingDataFlow: Flow<PagingData<Note>> = Pager(
    config = PagingConfig(pageSize = 20),
    pagingSourceFactory = { noteDAO.pagingSource() }
).flow
```

### Database Optimization

- Use indexes for frequently queried columns
- Limit query results with pagination
- Use transactions for bulk operations
- Cache frequently accessed data

### UI Optimization

- Use `remember` for expensive calculations
- Provide stable keys in LazyLists
- Avoid recomposition with `derivedStateOf`
- Use `LazyColumn` instead of `Column` for long lists

## Security

### Database Encryption

- **Android**: SQLCipher via SafeRoom
- **iOS**: SQLCipher via CocoaPods
- **Desktop**: Not implemented yet
- **Web**: Not supported (browser limitation)

### Best Practices

- Never hardcode passwords
- Use platform keystore for sensitive data
- Validate all user input
- Sanitize data before storage
- Follow OWASP mobile security guidelines

## References

- [Clean Architecture by Uncle Bob](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://github.com/JetBrains/compose-jb)
- [MVVM Pattern](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel)

