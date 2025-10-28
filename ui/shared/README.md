# UI Shared Module

## Overview

The `ui:shared` module contains the **shared Compose Multiplatform UI layer** for the NoteDelight application. It provides a complete, platform-independent UI implementation using Jetpack/Compose Multiplatform, ensuring consistent user experience across all supported platforms.

## Purpose

- Implement complete UI using Compose Multiplatform
- Provide reusable composable components
- Define app navigation graph with adaptive layouts
- Manage themes (Material 3, dark/light modes)
- Share UI code across all platforms (100% code sharing)
- Integrate with presentation layer (ViewModels)
- Support adaptive phone/tablet layouts with Material 3 Adaptive

## Architecture

```
ui:shared (UI Layer - Compose Multiplatform)
    ├── src/
    │   ├── commonMain/
    │   │   ├── kotlin/
    │   │   │   └── com/softartdev/notedelight/
    │   │   │       ├── App.kt                    # App entry point
    │   │   │       ├── di/
    │   │   │       │   └── koinModules.kt        # DI configuration
    │   │   │       └── ui/
    │   │   │           ├── main/                 # Notes list screen
    │   │   │           ├── note/                 # Note detail screen
    │   │   │           ├── splash/               # Splash screen
    │   │   │           ├── signin/               # Sign-in screen
    │   │   │           ├── title/                # Title edit dialog
    │   │   │           ├── settings/             # Settings screen
    │   │   │           │   └── security/         # Password screens
    │   │   │           ├── adaptive/             # Adaptive UI components
    │   │   │           ├── component/            # Reusable components
    │   │   │           └── theme/                # Theme & styling
    │   │   └── composeResources/                 # Shared resources
    │   │       ├── drawable/                     # Images, icons
    │   │       ├── values/                       # Strings, colors
    │   │       └── font/                         # Custom fonts
    │   ├── androidMain/                          # Android-specific UI
    │   ├── jvmMain/                              # Desktop-specific UI
    │   ├── iosMain/                              # iOS-specific UI
    │   └── wasmJsMain/                           # Web-specific UI
    └── build.gradle.kts
```

## Key Components

### App Entry Point (`App.kt`)

Main composable function initializing the app:

```kotlin
@Composable
fun App() {
    KoinContext {
        MaterialThemePrefs {
            NoteDelightTheme {
                AppNavigation()
            }
        }
    }
}
```

### Navigation (`navigation/`)

Type-safe navigation using Jetpack Navigation Compose:

```kotlin
sealed class Screen(val route: String) {
    object Main : Screen("main")
    data class Details(val noteId: Long) : Screen("details/$noteId")
    object Settings : Screen("settings")
    // ... other screens
}
```

### Screens (`ui/`)

Each screen is a composable function connected to a ViewModel:

#### Main Screen (`ui/main/`)
- `MainScreen.kt`: Notes list with pagination
- `NoteListItem.kt`: Individual note card component
- Shows notes in grid/list layout with search/filter

#### Note Screen (`ui/note/`)
- `NoteScreen.kt`: Note editor with rich text input
- `NoteToolbar.kt`: Edit/save/delete actions
- Auto-save functionality

#### Authentication (`ui/splash/`, `ui/signin/`)
- `SplashScreen.kt`: App initialization, database check
- `SignInScreen.kt`: Password entry for encrypted database

#### Settings (`ui/settings/`)
- `SettingsScreen.kt`: App settings
- `SecuritySettings.kt`: Password management
- **Password Management** (`ui/settings/security/`):
  - `EnterPasswordScreen.kt`: Set new password
  - `ConfirmPasswordScreen.kt`: Confirm password
  - `ChangePasswordScreen.kt`: Change existing password

#### Adaptive UI (`ui/adaptive/`)
- `AdaptiveScreen.kt`: Main adaptive layout with ListDetailPaneScaffold
- `PreviewAdaptiveScreen.kt`: Preview composable for adaptive layouts
- **Features**:
  - Responsive phone/tablet layouts
  - Material 3 Adaptive components
  - Three-pane navigation support
  - Automatic layout switching based on screen size

### Reusable Components (`ui/component/`)

Shared UI components used across screens:

- `AppTopBar.kt`: Consistent app bar
- `NoteCard.kt`: Note display card
- `EmptyState.kt`: Empty list placeholder
- `LoadingIndicator.kt`: Loading states
- `ErrorMessage.kt`: Error display
- `ConfirmDialog.kt`: Confirmation dialogs
- `SearchBar.kt`: Search input
- `FloatingActionButton.kt`: FAB for actions

### Theme (`ui/theme/`)

Material 3 theming with dark/light mode support:

```kotlin
@Composable
fun NoteDelightTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

- `Color.kt`: Color palette definitions
- `Typography.kt`: Text styles
- `Theme.kt`: Theme configuration

### Resources (`composeResources/`)

Multiplatform resources using Compose Resources API:

```
composeResources/
├── drawable/
│   └── ic_*.xml          # Vector icons
├── values/
│   ├── strings.xml       # Localized strings (English)
│   └── strings-ru.xml    # Localized strings (Russian)
└── font/
    └── *.ttf             # Custom fonts
```

## Dependency Injection

Uses **Koin** for dependency injection:

```kotlin
// di/koinModules.kt
val sharedModule = module {
    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { parameters -> NoteViewModel(get(), parameters.get(), get()) }
    // ... other ViewModels
}
```

## State Management Pattern

Follows **unidirectional data flow** with **Action Interface Pattern**:

```kotlin
// All screens use the Action interface pattern
@Composable
fun MainScreen(viewModel: MainViewModel = koinViewModel()) {
    val state by viewModel.stateFlow.collectAsState()
    
    MainScreenBody(
        state = state,
        onAction = viewModel::onAction  // Single action handler
    )
}

@Composable
fun MainScreenBody(
    state: NoteListResult,
    onAction: (MainAction) -> Unit = {}  // Simplified signature
) {
    when (state) {
        is NoteListResult.Loading -> LoadingIndicator()
        is NoteListResult.Success -> NotesList(
            notes = state.data,
            onNoteClick = { id -> onAction(MainAction.OnNoteClick(id)) }
        )
        is NoteListResult.Error -> ErrorMessage(state.message)
    }
    
    // Example: Settings button
    IconButton(onClick = { onAction(MainAction.OnSettingsClick) })
}
```

**Action Interface Benefits for UI**:
- ✅ **Reduced Parameters**: Composables take one `onAction` instead of many callbacks
- ✅ **Type Safety**: Actions are type-checked at compile time
- ✅ **Testability**: Easy to test with single action handler
- ✅ **Maintainability**: Adding new actions doesn't change signatures

**Important**: Not all screens use Actions! Simple screens with 1-2 actions (like `SignInScreen`) call ViewModel methods directly. The goal is pragmatic code reduction, not dogmatic pattern application.

## Multiplatform Support

This module provides **100% shared UI** across:

- ✅ **Android** - Native Android app
- ✅ **iOS** - Native iOS app (via Kotlin/Native + Compose)
- ✅ **Desktop** - JVM desktop app (Windows, macOS, Linux)
- ✅ **Web** - WebAssembly app (experimental)

Platform-specific code is minimal (window configuration, system bars, etc.).

## Dependencies

### Core Dependencies
- `core:domain` - Domain models
- `core:data` (db-sqldelight or db-room) - Data layer
- `core:presentation` - ViewModels
- `compose.ui` - Compose UI runtime
- `compose.material3` - Material 3 components
- `compose.materialIconsExtended` - Material icons
- `compose.components.resources` - Resource management

### Adaptive UI Dependencies
- `compose.material3.adaptive.layout` - Adaptive layout components
- `compose.material3.adaptive.navigation` - Adaptive navigation

### Navigation & Architecture
- `androidx.navigation.compose` - Navigation
- `androidx.lifecycle.runtime.compose` - Lifecycle integration
- `koin.compose.viewmodel.navigation` - DI integration

### Theme & Preferences
- `material.theme.prefs` - Theme preference persistence

### Pagination
- `thirdparty:androidx:paging:compose` - Paging for Compose

### Utilities
- `kotlinx-datetime` - Date/time display
- `napier` - Logging

### Platform-Specific
- **Android**: `androidx.activity.compose`, `androidx.ui.tooling` (previews)
- **JVM**: Koin core
- **iOS**: Stately (thread-safe state)

## UI Testing

UI testing is handled by the `ui:test-jvm` module (see below).

JVM tests can test Compose UI:

```kotlin
// In ui:shared/src/jvmTest/
@Test
fun testMainScreen() = runComposeUiTest {
    setContent {
        MainScreen()
    }
    
    onNodeWithText("Notes").assertIsDisplayed()
}
```

## Previews

Compose previews for Android Studio:

```kotlin
@Preview
@Composable
fun MainScreenPreview() {
    NoteDelightTheme {
        MainScreen()
    }
}
```

## Localization

Strings are defined in XML resources:

```xml
<!-- values/strings.xml -->
<resources>
    <string name="app_name">Note Delight</string>
    <string name="create_note">Create Note</string>
    <!-- ... -->
</resources>

<!-- values-ru/strings.xml -->
<resources>
    <string name="app_name">Note Delight</string>
    <string name="create_note">Создать заметку</string>
    <!-- ... -->
</resources>
```

Usage in Compose:

```kotlin
Text(text = stringResource(Res.string.app_name))
```

## Theme Customization

Material 3 dynamic theming:

```kotlin
val ColorScheme.customColor: Color
    get() = if (this == DarkColorScheme) {
        Color(0xFF6200EE)
    } else {
        Color(0xFF3700B3)
    }
```

## Adaptive UI Guidelines

### Material 3 Adaptive Components

Use adaptive components for responsive layouts:

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

### Responsive Design Principles

1. **Use adaptive components**: `ListDetailPaneScaffold`, `ThreePaneScaffoldNavigator`
2. **Handle different screen sizes**: Phone vs tablet layouts
3. **Test on multiple devices**: Use preview devices for testing
4. **Maintain navigation state**: Proper state management across layout changes

## AI Agent Guidelines

**See [CONTRIBUTING.md](../CONTRIBUTING.md) for general guidelines.**

### UI-Specific
- State hoisting to appropriate level
- Small, single-purpose composables
- No business logic in UI
- Use ViewModels via Koin
- Material 3 components consistently
- Adaptive components for responsive layouts

## Best Practices

**See [CONTRIBUTING.md](../CONTRIBUTING.md) for general coding guidelines.**

### UI-Specific Patterns
- Use `remember` for expensive calculations
- Provide keys for lists (`LazyColumn`, `LazyRow`)
- Use `derivedStateOf` for computed values
- Use `LazyColumn` instead of `Column` for long lists

## Related Modules

- **Used by**: `app:android`, `app:desktop`, `app:web`, `app:ios-kit`
- **Depends on**: `core:domain`, `core:presentation`, `core:data`, `thirdparty:androidx:paging:compose`
- **Tested by**: `ui:test-jvm`

## Building & Running

```bash
./gradlew :ui:shared:build    # Build module
./gradlew :ui:shared:test     # Run tests
```

