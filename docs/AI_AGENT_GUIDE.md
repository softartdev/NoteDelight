# AI Agent Development Guide

This guide is specifically written for AI agents working on the NoteDelight codebase. It provides structured information to help you understand the project and contribute effectively.

## Quick Reference

### Project Type
- **Kotlin Multiplatform Mobile (KMP)** application
- **Compose Multiplatform** for UI
- **Clean Architecture** with MVVM pattern

### Key Commands

```bash
./gradlew build                    # Build everything
./gradlew test                     # Run tests
./gradlew :app:android:installDebug # Android app
./gradlew :app:desktop:run         # Desktop app
./gradlew :app:web:wasmJsBrowserDevelopmentRun --continuous  # Web app
```

### Version Management

**See [VERSION_MANAGEMENT_GUIDE.md](VERSION_MANAGEMENT_GUIDE.md) for detailed instructions and commands.**

## Understanding the Codebase

### Module Hierarchy (Dependency Order)

```
Level 1 (No dependencies):
└── core:domain

Level 2 (Depends on domain):
├── core:presentation
├── core:data:db-sqldelight
└── core:data:db-room

Level 3 (Depends on presentation/data):
├── ui:shared
└── core:test

Level 4 (Integration):
├── ui:test
├── ui:test-jvm
└── app:ios-kit

Level 5 (Platform apps):
├── app:android
├── app:desktop
├── app:iosApp
└── app:web
```

### Reading Order for Understanding

1. **Start here**: `core/domain` - Pure business logic
2. **Then**: `core/presentation` - ViewModels and state
3. **Then**: `ui/shared` - UI implementation
4. **Then**: `core/data/db-sqldelight` - Data persistence
5. **Finally**: `app/*` - Platform integration

### Key Files to Read First

```
core/domain/src/commonMain/kotlin/
└── com/softartdev/notedelight/
    ├── model/Note.kt                    # Main entity
    ├── db/NoteDAO.kt                    # Data access interface
    └── usecase/note/CreateNoteUseCase.kt # Example use case

core/presentation/src/commonMain/kotlin/
└── com/softartdev/notedelight/
    ├── presentation/main/MainViewModel.kt # Example ViewModel
    └── navigation/Router.kt              # Navigation abstraction

ui/shared/src/commonMain/kotlin/
└── com/softartdev/notedelight/
    ├── App.kt                           # App entry point
    └── ui/main/MainScreen.kt            # Example screen
```

## Common Tasks

### Task: Add a New Feature

#### Step 1: Domain Layer (Business Logic)

```kotlin
// 1. Define entity/model if needed
// core/domain/src/commonMain/kotlin/model/YourEntity.kt
data class YourEntity(val id: Long, val name: String)

// 2. Add method to DAO interface
// core/domain/src/commonMain/kotlin/db/NoteDAO.kt
interface NoteDAO {
    suspend fun yourNewMethod(): List<YourEntity>
}

// 3. Create use case
// core/domain/src/commonMain/kotlin/usecase/YourUseCase.kt
class YourUseCase(private val dao: NoteDAO) {
    suspend operator fun invoke(): List<YourEntity> {
        // Business logic here
        return dao.yourNewMethod()
    }
}
```

#### Step 2: Data Layer (Implementation)

```kotlin
// Implement DAO method
// core/data/db-sqldelight/src/commonMain/kotlin/NoteSQLDelightDAO.kt
override suspend fun yourNewMethod(): List<YourEntity> {
    return database.yourQueries.select()
        .executeAsList()
        .map { it.toDomainModel() }
}

// Add SQL query
// core/data/db-sqldelight/src/commonMain/sqldelight/.../NoteDb.sq
yourNewQuery:
SELECT * FROM YourTable WHERE condition = ?;
```

#### Step 3: Presentation Layer (ViewModel)

```kotlin
// Add ViewModel
// core/presentation/src/commonMain/kotlin/presentation/YourViewModel.kt
class YourViewModel(
    private val useCase: YourUseCase,
    private val router: Router
) : ViewModel() {
    
    private val _stateFlow = MutableStateFlow<YourState>(YourState.Loading)
    val stateFlow: StateFlow<YourState> = _stateFlow
    
    fun loadData() {
        viewModelScope.launch {
            try {
                val data = useCase()
                _stateFlow.value = YourState.Success(data)
            } catch (e: Exception) {
                _stateFlow.value = YourState.Error(e.message)
            }
        }
    }
}

// Define state
sealed class YourState {
    object Loading : YourState()
    data class Success(val data: List<YourEntity>) : YourState()
    data class Error(val message: String?) : YourState()
}
```

#### Step 4: UI Layer (Composable)

```kotlin
// Add screen
// ui/shared/src/commonMain/kotlin/ui/YourScreen.kt
@Composable
fun YourScreen(viewModel: YourViewModel = koinViewModel()) {
    val state by viewModel.stateFlow.collectAsState()
    
    when (val currentState = state) {
        is YourState.Loading -> CircularProgressIndicator()
        is YourState.Success -> YourContent(currentState.data)
        is YourState.Error -> ErrorMessage(currentState.message)
    }
}

@Composable
private fun YourContent(data: List<YourEntity>) {
    LazyColumn {
        items(data) { item ->
            Text(item.name)
        }
    }
}
```

#### Step 5: Dependency Injection

```kotlin
// Add to Koin module
// ui/shared/src/commonMain/kotlin/di/koinModules.kt
val sharedModule = module {
    factory { YourUseCase(get()) }
    viewModel { YourViewModel(get(), get()) }
}
```

#### Step 6: Testing

```kotlin
// Test ViewModel
// core/presentation/src/androidUnitTest/kotlin/YourViewModelTest.kt
class YourViewModelTest {
    @Test
    fun `loading data should update state to Success`() = runTest {
        val mockUseCase = mock<YourUseCase>()
        whenever(mockUseCase()).thenReturn(listOf())
        
        val viewModel = YourViewModel(mockUseCase, mock())
        viewModel.loadData()
        
        assertTrue(viewModel.stateFlow.value is YourState.Success)
    }
}
```

### Task: Fix a Bug

1. **Locate the bug**: Search for error message or affected feature
2. **Identify layer**: Determine which layer has the issue
3. **Write failing test**: Add test that reproduces the bug
4. **Fix the bug**: Implement fix
5. **Verify**: Ensure test passes
6. **Check impact**: Test affected features

### Task: Refactor Code

1. **Read existing code**: Understand current implementation
2. **Write tests first**: Ensure behavior is tested
3. **Refactor incrementally**: Small, safe changes
4. **Run tests frequently**: After each change
5. **Verify all platforms**: Test on Android, Desktop, etc.

## Pattern Recognition

### Identifying Patterns

When you see this structure:
```kotlin
class SomethingViewModel(
    private val someUseCase: SomeUseCase,
    private val router: Router
) : ViewModel() {
    private val _stateFlow = MutableStateFlow<SomeState>(...)
    val stateFlow: StateFlow<SomeState> = _stateFlow
}
```

**You know**: This is a ViewModel following the project's pattern. It should:
- Use `StateFlow` for state
- Have private mutable `_stateFlow`, public immutable `stateFlow`
- Receive dependencies via constructor
- Use use cases for business logic
- Use router for navigation

### Common Patterns in This Codebase

#### Use Case Pattern
```kotlin
class VerbNounUseCase(private val dao: DAO) {
    suspend operator fun invoke(params): Result {
        // Single responsibility business logic
    }
}
```

#### State Pattern
```kotlin
sealed class FeatureResult {
    object Loading : FeatureResult()
    data class Success(val data: Data) : FeatureResult()
    data class Error(val message: String?) : FeatureResult()
}
```

#### Screen Pattern with Action Interface

**All ViewModels in this project use the Action interface pattern** to avoid callback hell and reduce `@Composable` function signatures.

**Refer to [docs/ARCHITECTURE.md](ARCHITECTURE.md#presentation-layer) for the full pattern definition, code examples, and guidelines on when to use it.**

## Debugging Strategies

### For Build Errors

1. **Check module dependencies**: Verify in `build.gradle.kts`
2. **Check platform support**: Ensure code works on target platform
3. **Check imports**: Verify correct imports for platform
4. **Clean build**: `./gradlew clean build`

### For Runtime Errors

1. **Check logs**: Look for stack traces
2. **Check initialization**: Verify Koin modules initialized
3. **Check null safety**: Look for nullable types
4. **Check coroutine context**: Verify correct dispatcher

### For UI Issues

1. **Check state**: Print state in ViewModel
2. **Check composition**: Verify composable is called
3. **Check recomposition**: Ensure state triggers recomposition
4. **Check modifier**: Verify Modifier chain is correct

## Code Review Checklist for AI Agents

**See [CONTRIBUTING.md](../CONTRIBUTING.md) for detailed guidelines.**

### Quick Checklist
- [ ] Architecture boundaries respected
- [ ] Tests added/updated
- [ ] All platforms work
- [ ] No hardcoded strings
- [ ] Error handling included

## Common Mistakes to Avoid

### ❌ Wrong: Business Logic in ViewModel
```kotlin
class MyViewModel : ViewModel() {
    fun createNote(title: String) {
        // ❌ Business logic in ViewModel
        val id = generateId()
        val note = Note(id, title, ...)
        noteDAO.insert(note)
    }
}
```

### ✅ Correct: Business Logic in Use Case
```kotlin
class CreateNoteUseCase(private val dao: NoteDAO) {
    suspend operator fun invoke(title: String): Long {
        // ✅ Business logic in use case
        val id = generateId()
        val note = Note(id, title, ...)
        dao.insert(note)
        return id
    }
}

class MyViewModel(private val createNote: CreateNoteUseCase) : ViewModel() {
    fun createNote(title: String) {
        viewModelScope.launch {
            createNote(title)
        }
    }
}
```

### ❌ Wrong: Direct DAO Access from ViewModel
```kotlin
class MyViewModel(private val noteDAO: NoteDAO) : ViewModel() {
    // ❌ ViewModel shouldn't access DAO directly
}
```

### ✅ Correct: ViewModel Uses Use Cases
```kotlin
class MyViewModel(
    private val createNote: CreateNoteUseCase,
    private val loadNotes: LoadNotesUseCase
) : ViewModel() {
    // ✅ ViewModel uses use cases
}
```

### ❌ Wrong: LiveData for State
```kotlin
private val _stateLiveData = MutableLiveData<State>()
val stateLiveData: LiveData<State> = _stateLiveData
```

### ✅ Correct: StateFlow for State
```kotlin
private val _stateFlow = MutableStateFlow<State>(State.Initial)
val stateFlow: StateFlow<State> = _stateFlow
```

## Platform-Specific Considerations

### Android
- Use `androidMain/` for Android-specific code
- Android SDK APIs available
- SQLCipher supported
- Instrumented tests available

### iOS
- Use `iosMain/` for iOS-specific code
- Use `actual` for platform implementations
- SQLCipher via CocoaPods
- Memory model considerations (use Stately)

### Desktop
- Use `jvmMain/` for Desktop-specific code
- Full JVM APIs available
- No SQLCipher yet
- Compose Desktop for UI

### Web
- Use `wasmJsMain/` for Web-specific code
- Browser APIs only
- No SQLCipher yet
- SQL.js for database

## Tips for Efficient Development

### Use Existing Patterns
Don't reinvent the wheel. Find similar features and copy the pattern:
- New screen? Copy `MainScreen.kt` structure
- New ViewModel? Copy `MainViewModel.kt` structure
- New use case? Copy `CreateNoteUseCase.kt` structure

### Leverage Type System
Let Kotlin's type system guide you:
- Sealed classes for exhaustive `when`
- Nullable types for optional values
- Suspend functions for async operations

### Use IDE Features
- **Go to Definition**: Understand implementation
- **Find Usages**: See how code is used
- **Hierarchy**: Understand class/interface relationships
- **Structure**: Navigate within files

### Read Tests
Tests often show how to use code correctly. Look at:
- `core/presentation/src/androidUnitTest/` for ViewModel examples
- `ui/test/src/commonMain/kotlin/ui/cases/` for multiplatform UI test examples
- `ui/test-jvm/src/main/kotlin/` for JVM-specific test utilities

## Getting Unstuck

### I don't understand how X works
1. Find existing usage of X in codebase
2. Read tests for X
3. Check module README.md
4. Look at similar features

### I need to add platform-specific code
1. Check if `expect`/`actual` already exists
2. Look at `util/` packages for examples
3. Add to appropriate `*Main/` source set
4. Implement for all required platforms

### Tests are failing
1. Read error message carefully
2. Run specific test: `./gradlew :module:test --tests "TestName"`
3. Add print statements in test
4. Check if setup/teardown needed

### Build is failing
1. Read error message (first error, not last)
2. Check if all dependencies available
3. Try `./gradlew clean build`
4. Check platform compatibility

## Resources

- **Module READMEs**: Each module has detailed README
- **Architecture**: [ARCHITECTURE.md](ARCHITECTURE.md)
- **Contributing**: [CONTRIBUTING.md](../CONTRIBUTING.md)
- **Testing**: [TESTING_GUIDE.md](TESTING_GUIDE.md)
- **Version Management**: [VERSION_MANAGEMENT_GUIDE.md](VERSION_MANAGEMENT_GUIDE.md)

## Quick Decision Tree

### Adding new code?
```
New feature?
├─ Yes → Start in domain layer (use case)
└─ No → Bug fix → Find affected layer

Adding UI?
├─ New screen → Create ViewModel first
└─ New component → Create in ui/component/

Need data access?
├─ New query → Add to DAO interface (domain)
└─ Implement in data layer

Adding logic?
├─ Business logic → Use case (domain)
├─ UI state → ViewModel (presentation)
├─ UI rendering → Composable (ui)
└─ Data access → DAO (data)
```

## Summary

**Remember**: 
- Follow the architecture (domain → presentation → data → ui)
- Copy existing patterns
- Test your changes
- Check all platforms
- Read module READMEs

**When in doubt**:
- Look at similar existing code
- Follow the dependency rule (inward dependencies)
- Ask: "Which layer is responsible for this?"

Good luck! 🤖

