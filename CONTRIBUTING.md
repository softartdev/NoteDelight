# Contributing to NoteDelight

Welcome! This document provides guidelines for contributing to NoteDelight, whether you're a human developer or an AI agent. The project is designed to be understandable and maintainable by both.

## Table of Contents

- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Code Style](#code-style)
- [Module Structure](#module-structure)
- [Development Workflow](#development-workflow)
- [Testing Guidelines](#testing-guidelines)
- [Commit Guidelines](#commit-guidelines)
- [Pull Request Process](#pull-request-process)

## Project Overview

NoteDelight is a **Kotlin Multiplatform** note-taking application with database encryption support, built with:

- **Clean Architecture** - Separation of concerns across layers
- **MVVM Pattern** - Presentation layer architecture
- **Compose Multiplatform** - 100% shared UI code with adaptive layouts
- **Material 3 Adaptive** - Responsive phone/tablet layouts
- **SQLDelight/Room** - Swappable data layer implementations
- **Koin** - Dependency injection
- **Coroutines & Flow** - Asynchronous programming

### Supported Platforms

- ✅ Android (minSdk 24)
- ✅ iOS (14.0+)
- ✅ Desktop (Windows, macOS, Linux)
- ✅ Web (WebAssembly, experimental)

## Architecture

**See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for complete architecture documentation.**

## Code Style

### Kotlin Style Guide

We follow the **official Kotlin coding conventions**:

```kotlin
kotlin.code.style=official
```

### Naming Conventions

#### Packages
- **lowercase** with dots: `com.softartdev.notedelight.feature`
- No underscores

#### Classes & Objects
- **PascalCase**: `NoteViewModel`, `CreateNoteUseCase`
- Interfaces: No "I" prefix - `NoteDAO` not `INoteDAO`
- Implementations: Descriptive names - `NoteSQLDelightDAO`

#### Functions & Properties
- **camelCase**: `createNote()`, `noteTitle`
- Boolean properties: `isEnabled`, `hasItems`
- Observable properties: `stateFlow`, `noteListFlow`

#### Constants
- **UPPER_SNAKE_CASE**: `MAX_NOTE_LENGTH`, `DATABASE_NAME`

#### Files
- One public top-level type per file
- Filename matches the type name
- Extensions in separate files: `StringExt.kt`, `FlowExt.kt`

### Formatting Rules

#### Indentation
- **4 spaces** (no tabs)
- Continuation indent: 4 spaces

#### Line Length
- Preferred max: **120 characters**
- Hard limit: **150 characters**

#### Imports
- No wildcard imports (except in tests)
- Organized and optimized
- Android Studio organize imports: ⌥⌘O (Mac) / Ctrl+Alt+O (Windows)

#### Blank Lines
- One blank line between functions
- Two blank lines between top-level declarations
- No blank lines at start/end of blocks

### Code Organization

#### Class Structure Order

```kotlin
class MyClass {
    // 1. Companion object
    companion object {
        const val CONSTANT = "value"
    }
    
    // 2. Properties
    private val privateProperty: String
    val publicProperty: String
    
    // 3. Init blocks
    init {
        // Initialization
    }
    
    // 4. Secondary constructors
    constructor(param: String) : this(param, "default")
    
    // 5. Functions (public first, then private)
    fun publicFunction() { }
    
    private fun privateFunction() { }
    
    // 6. Nested classes/objects
    inner class NestedClass
}
```

#### Function Structure

```kotlin
fun functionName(
    parameter1: Type1,
    parameter2: Type2
): ReturnType {
    // Early returns for error cases
    if (invalidCondition) return defaultValue
    
    // Main logic
    val result = processData(parameter1, parameter2)
    
    // Return result
    return result
}
```

### Adaptive UI Guidelines

#### Material 3 Adaptive Components

Use Material 3 adaptive components for responsive layouts:

```kotlin
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AdaptiveScreen() {
    val navigator = rememberListDetailPaneScaffoldNavigator<Long>()
    
    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = { ListContent() },
        detailPane = { DetailContent() }
    )
}
```

#### Adaptive Navigation

```kotlin
class RouterImpl : Router {
    private var adaptiveNavigator: ThreePaneScaffoldNavigator<Long>? = null
    
    override fun setAdaptiveNavigator(adaptiveNavigator: Any) {
        this.adaptiveNavigator = adaptiveNavigator as ThreePaneScaffoldNavigator<Long>
    }
    
    override suspend fun adaptiveNavigateToDetail(contentKey: Long?) {
        adaptiveNavigator!!.navigateTo(ListDetailPaneScaffoldRole.Detail, contentKey)
    }
}
```

#### Responsive Design Principles

1. **Use adaptive components**: `ListDetailPaneScaffold`, `ThreePaneScaffoldNavigator`
2. **Handle different screen sizes**: Phone vs tablet layouts
3. **Test on multiple devices**: Use preview devices for testing
4. **Maintain navigation state**: Proper state management across layout changes

### Compose Style

#### Composable Functions

```kotlin
@Composable
fun ScreenName(
    viewModel: ScreenViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
    onNavigate: (Destination) -> Unit
) {
    // State
    val state by viewModel.stateFlow.collectAsState()
    
    // UI
    ScreenContent(
        state = state,
        onAction = viewModel::handleAction,
        modifier = modifier
    )
}

@Composable
private fun ScreenContent(
    state: ScreenState,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier
) {
    // Stateless implementation
}
```

#### Composable Guidelines

1. **State hoisting**: Hoist state to appropriate level
2. **Stateless when possible**: Prefer stateless composables
3. **Modifier parameter**: Always last parameter with default value
4. **Preview functions**: Add `@Preview` for visual components
5. **Remember wisely**: Use `remember` for expensive calculations
6. **Keys in lists**: Provide stable keys for LazyColumn/LazyRow

### ViewModel Style

```kotlin
class ScreenViewModel(
    private val useCase: UseCase,
    private val router: Router,
    private val dispatchers: CoroutineDispatchers
) : ViewModel() {
    
    private val logger = Logger.withTag("ScreenViewModel")

    // Private mutable state
    private val _stateFlow = MutableStateFlow<ScreenState>(ScreenState.Initial)
    
    // Public immutable state
    val stateFlow: StateFlow<ScreenState> = _stateFlow
    
    // Event handlers (public functions)
    fun onAction() {
        viewModelScope.launch(dispatchers.io) {
            try {
                val result = useCase()
                _stateFlow.value = ScreenState.Success(result)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }
    
    // Private helpers
    private fun handleError(error: Throwable) {
        logger.e(error) { "Error" }
        _stateFlow.value = ScreenState.Error(error.message)
    }
}
```

### Logging Guidelines

We use **Kermit** for logging.

- **Initialization**: `Logger` is initialized in `MainApplication.kt` (Android), `Main.kt` (Desktop), `main.kt` (Web), and `AppHelper.kt` (iOS).
- **Tags**: Unlike Napier, Kermit does not automatically infer tags from class names.
    - The default tag is set to "NoteDelight".
    - To use a custom tag, use `Logger.withTag("MyTag")` or `Logger.d(tag = "MyTag") { ... }`.
    - For classes, you can define a logger property: `private val logger = Logger.withTag("MyClass")`.
- **Lambda usage**: Always pass the message in a lambda to avoid unnecessary string construction if logging is disabled.
    - Good: `Logger.d { "Message with $param" }`
    - Bad: `Logger.d("Message with $param")`

### Use Case Style

```kotlin
class CreateNoteUseCase(
    private val noteDAO: NoteDAO
) {
    suspend operator fun invoke(title: String, text: String): Long {
        // Validation
        require(title.isNotBlank()) { "Title cannot be blank" }
        
        // Business logic
        val note = Note(
            id = generateId(),
            title = title,
            text = text,
            dateCreated = createLocalDateTime(),
            dateModified = createLocalDateTime()
        )
        
        // Persistence
        noteDAO.insert(note)
        
        return note.id
    }
}
```

### Testing Style

```kotlin
class FeatureViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    private lateinit var viewModel: FeatureViewModel
    private lateinit var mockUseCase: UseCase
    
    @Before
    fun setup() {
        mockUseCase = mock()
        viewModel = FeatureViewModel(mockUseCase, ...)
    }
    
    @Test
    fun `given initial state when action performed then state updates`() = runTest {
        // Given
        val expectedResult = "result"
        whenever(mockUseCase()).thenReturn(expectedResult)
        
        // When
        viewModel.performAction()
        
        // Then
        val state = viewModel.stateFlow.value
        assertTrue(state is ScreenState.Success)
        assertEquals(expectedResult, (state as ScreenState.Success).data)
    }
}
```

## Module Structure

### Core Modules

- **core:domain** - Pure business logic, no dependencies
- **core:presentation** - ViewModels, state management
- **core:data:db-sqldelight** - SQLDelight data implementation (default)
- **core:data:db-room** - Room data implementation (alternative)
- **core:test** - Shared test utilities

### UI Modules

- **ui:shared** - Shared Compose UI (100% shared)
- **ui:test-jvm** - UI testing framework (Kaspresso-inspired)

### App Modules

- **app:android** - Android application
- **app:desktop** - Desktop JVM application
- **app:web** - Web/Wasm application
- **app:ios-kit** - iOS framework (CocoaPods)
- **app:iosApp** - iOS application (Swift/SwiftUI)

### Build Modules

- **build-logic** - Gradle convention plugins
- **thirdparty** - Vendored dependencies

### Module Guidelines

1. **One purpose per module**: Single responsibility
2. **Clear dependencies**: Explicit, minimal dependencies
3. **Public API**: Carefully design public APIs
4. **Internal implementation**: Use `internal` for implementation details
5. **README**: Every module must have a README.md

## Development Workflow

### Setting Up Development Environment

1. **Clone repository**
```bash
git clone https://github.com/softartdev/NoteDelight.git
cd NoteDelight
```

2. **Open in IDE**
   - Android Studio (recommended for Android/multiplatform)
   - IntelliJ IDEA (good for Desktop/JVM)
   - Xcode (required for iOS)

3. **Build project**
```bash
./gradlew build
```

4. **Run tests**
```bash
./gradlew test
```

### Platform-Specific Setup

#### Android
```bash
./gradlew :app:android:assembleDebug
./gradlew :app:android:installDebug
```

#### Desktop
```bash
./gradlew :app:desktop:run
```

#### iOS
```bash
cd app/iosApp
pod install
open iosApp.xcworkspace
# Build and run in Xcode
```

#### Web
```bash
./gradlew :app:web:wasmJsBrowserDevelopmentRun --continuous
```

### Switching Database Implementation

Edit `gradle.properties`:

```properties
# Use SQLDelight (default)
CORE_DATA_DB_MODULE=:core:data:db-sqldelight

# Or use Room
CORE_DATA_DB_MODULE=:core:data:db-room
```

Then rebuild:
```bash
./gradlew clean build
```

## Testing Guidelines

**See [docs/TESTING_GUIDE.md](docs/TESTING_GUIDE.md) for complete testing documentation.**

### Quick Commands
```bash
./gradlew test                    # All tests
./gradlew :app:android:connectedCheck  # Android UI tests
./gradlew :app:desktop:jvmTest    # Desktop tests
```

## Commit Guidelines

### Commit Message Format

```
<module>: <summary>

<body>

<footer>
```

### Examples

```
core/domain: Add pagination support to NoteDAO

Implement Flow<PagingData<Note>> for efficient note list loading
with support for all platforms.

Closes #123
```

```
ui/shared: Fix dark theme colors in note editor

- Update Material 3 color scheme
- Fix text visibility issues
- Add contrast for better accessibility
```

### Rules

1. **Imperative mood**: "Add feature" not "Added feature"
2. **Max 72 chars**: Keep subject line concise
3. **Module prefix**: Start with affected module
4. **Body optional**: Add details if needed
5. **Footer**: Reference issues (Closes #123, Fixes #456)

### Commit Size

- **Small, focused commits**: One logical change per commit
- **Atomic**: Each commit should build and pass tests
- **Reversible**: Easy to revert if needed

## Pull Request Process

### Before Submitting

1. ✅ **Build passes**: `./gradlew build`
2. ✅ **Tests pass**: `./gradlew test`
3. ✅ **Lints fixed**: No new lint errors
4. ✅ **Format code**: Follow code style
5. ✅ **Update docs**: Update README if needed
6. ✅ **Self-review**: Review your own changes first

### PR Description Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] UI tests added/updated
- [ ] Manual testing performed

## Platforms Tested
- [ ] Android
- [ ] iOS
- [ ] Desktop
- [ ] Web

## Screenshots (if UI changes)
[Add screenshots here]

## Checklist
- [ ] Code follows project style guide
- [ ] Self-review completed
- [ ] Comments added for complex logic
- [ ] Documentation updated
- [ ] No new warnings introduced
```

### Review Process

1. **Automated checks**: CI/CD must pass
2. **Code review**: At least one approval
3. **Testing**: Reviewer tests changes
4. **Documentation**: Verify docs updated
5. **Merge**: Squash or merge based on commits

### For Reviewers

Look for:
- ✅ Code style compliance
- ✅ Architecture adherence
- ✅ Test coverage
- ✅ Performance implications
- ✅ Security concerns
- ✅ Breaking changes
- ✅ Documentation quality

## Continuous Integration

### GitHub Actions Workflows

- **kmp.yml**: Kotlin Multiplatform build and test
- **android.yml**: Android app build and deployment
- **ios.yml**: iOS app build and deployment
- **desktop.yaml**: Desktop app packaging
- **web.yml**: Web app deployment

### Pre-commit Checks

```bash
# Run before committing
./gradlew build test
```

### CI Requirements

All PRs must pass:
- ✅ Compilation (all platforms)
- ✅ Unit tests
- ✅ Android lint
- ✅ Code style checks

## AI Agent Guidelines

**See [docs/AI_AGENT_GUIDE.md](docs/AI_AGENT_GUIDE.md) for comprehensive AI agent documentation.**

## Getting Help

- **Documentation**: Check module READMEs
- **Issues**: Search existing issues
- **Discussions**: GitHub Discussions for questions
- **Code examples**: Look at existing implementations

## Security

- ❌ **Never commit**: API keys, passwords, keystores, certificates
- ✅ **Use**: Local files for signing configuration:
  - `app/android/keystore.properties` - Android signing config
  - `app/desktop/keystore.properties` - Desktop macOS signing config
  - `local.properties` - Local SDK paths
- ✅ **Sanitize**: Firebase configs before committing
- ✅ **Review**: Check for accidental secrets before committing
- ✅ **Encrypted secrets**: Real signing credentials are stored encrypted in `.github/secrets/*.gpg` for CI/CD

## License

By contributing, you agree that your contributions will be licensed under the project's license (see LICENSE file).

## Code of Conduct

Be respectful, constructive, and collaborative. We welcome contributions from everyone.

---

Thank you for contributing to NoteDelight! 🎉
