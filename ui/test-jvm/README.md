# UI Test JVM Module

## Overview

The `ui:test-jvm` module provides a **comprehensive UI testing framework** for Compose Multiplatform applications, specifically designed for **JVM-based platforms** (Android and Desktop). The testing approach is **inspired by the Kaspresso library**, using screen objects, test cases, and fluent DSL for readable and maintainable UI tests.

## Purpose

- Provide reusable UI testing infrastructure for Android and Desktop
- Implement **Kaspresso-inspired** testing patterns
- Define screen objects (Page Object Model)
- Create reusable test cases for common user flows
- Enable consistent UI testing across JVM platforms
- Support future expansion to other platforms

## Architecture

```
ui:test-jvm (UI Testing Framework - Kaspresso-inspired)
    ├── src/
    │   └── main/
    │       └── kotlin/
    │           └── com/softartdev/notedelight/
    │               ├── ui/
    │               │   ├── AbstractUiTests.kt         # Base test class
    │               │   ├── BaseTestCase.kt            # Base test case
    │               │   ├── cases/                     # Reusable test cases
    │               │   │   ├── CrudTestCase.kt
    │               │   │   ├── EditTitleAfterCreateTestCase.kt
    │               │   │   ├── EditTitleAfterSaveTestCase.kt
    │               │   │   ├── FlowAfterCryptTestCase.kt
    │               │   │   ├── PrepopulateDbTestCase.kt
    │               │   │   ├── SettingPasswordTestCase.kt
    │               │   │   └── SignInTestCase.kt
    │               │   └── screen/                    # Screen objects (Page Objects)
    │               │       ├── MainTestScreen.kt
    │               │       ├── NoteScreen.kt
    │               │       ├── SettingsTestScreen.kt
    │               │       ├── SignInScreen.kt
    │               │       └── dialog/
    │               │           ├── ChangePasswordDialog.kt
    │               │           ├── ConfirmPasswordDialog.kt
    │               │           ├── EditTitleDialog.kt
    │               │           ├── EnterPasswordDialog.kt
    │               │           └── CommonDialog.kt
    │               ├── di/
    │               │   └── uiTestModules.kt           # Test DI modules
    │               ├── DbTestEncryptor.kt             # Database test utilities
    │               ├── TestLifecycleOwner.kt          # Lifecycle for tests
    │               ├── UiThreadRouter.kt              # Test navigation
    │               ├── ext.kt                         # Extension functions
    │               └── runOnUiThread.kt               # UI thread utilities
    └── build.gradle.kts
```

## Kaspresso-Inspired Design

### What is Kaspresso?

[Kaspresso](https://github.com/KasperskyLab/Kaspresso) is a popular Android UI testing framework that provides:
- **Fluent DSL** for readable tests
- **Screen objects** (Page Object Model)
- **Test cases** for reusable scenarios
- **Automatic flakiness handling**
- **Readable test reports**

### Our Adaptation

This module adapts Kaspresso's principles for **Compose Multiplatform**:

1. **Screen Objects**: Page Object Model for screens
2. **Test Cases**: Reusable test scenarios
3. **Fluent DSL**: Readable, expressive test syntax
4. **Abstract Base**: Common test infrastructure
5. **Platform Support**: Works on Android and Desktop (with future iOS/Web support)

## Key Components

### Abstract Test Base (`AbstractUiTests.kt`)

Base class for all UI tests:

```kotlin
abstract class AbstractUiTests {
    abstract val composeTestRule: ComposeContentTestRule
    
    // Lifecycle
    open fun setUp() = Unit
    open fun tearDown() = Unit
    
    // Test cases (can be overridden per platform)
    open fun crudNoteTest() = CrudTestCase(composeTestRule).invoke()
    open fun editTitleAfterCreateTest() = EditTitleAfterCreateTestCase(composeTestRule).invoke()
    // ... more test cases
    
    // Platform-specific actions
    abstract fun pressBack()
    abstract fun closeSoftKeyboard()
}
```

### Screen Objects (`screen/`)

**Page Object Model** for each screen:

```kotlin
class MainTestScreen(private val composeTestRule: ComposeContentTestRule) {
    val fabSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithContentDescription("Create Note")
    
    val noteListItemSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithText(noteItemTitleText)
    
    val emptyResultLabelSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithText("No notes yet")
    
    // Screen context for fluent DSL
    fun screen(block: MainTestScreen.() -> Unit) = apply(block)
    
    companion object {
        var noteItemTitleText: String = ""
    }
}
```

#### Available Screen Objects

1. **MainTestScreen**: Notes list screen
2. **NoteScreen**: Note editor screen
3. **SignInScreen**: Authentication screen
4. **SettingsTestScreen**: Settings screen
5. **Dialog Screens**:
   - `CommonDialog`: Generic dialogs
   - `EditTitleDialog`: Title editing
   - `EnterPasswordDialog`: Enter password
   - `ConfirmPasswordDialog`: Confirm password
   - `ChangePasswordDialog`: Change password

### Test Cases (`cases/`)

**Reusable test scenarios** following functional test case pattern:

```kotlin
class CrudTestCase(
    composeTestRule: ComposeContentTestRule
) : () -> Unit, BaseTestCase(composeTestRule) {
    
    override fun invoke() = runTest {
        mainTestScreen {
            composeTestRule.waitUntilDisplayed(blockSNI = ::fabSNI)
            fabSNI.performClick()
            
            noteScreen {
                textFieldSNI.performTextInput(actualNoteText)
                saveNoteMenuButtonSNI.performClick()
                backButtonSNI.performClick()
            }
            
            noteItemTitleText = actualNoteText
            composeTestRule.waitUntilDisplayed(blockSNI = ::noteListItemSNI)
            noteListItemSNI.performClick()
            
            noteScreen {
                deleteNoteMenuButtonSNI.performClick()
                commonDialog {
                    yesDialogButtonSNI.performClick()
                }
            }
            
            composeTestRule.waitUntilDisplayed(blockSNI = ::emptyResultLabelSNI)
        }
    }
}
```

#### Available Test Cases

1. **CrudTestCase**: Create, Read, Update, Delete note flow
2. **EditTitleAfterCreateTestCase**: Edit note title immediately after creation
3. **EditTitleAfterSaveTestCase**: Edit note title after saving
4. **PrepopulateDbTestCase**: Pre-populate database with test data
5. **FlowAfterCryptTestCase**: Test encryption/decryption flow
6. **SettingPasswordTestCase**: Set database password
7. **SignInTestCase**: Sign in with password

### Fluent DSL Pattern

Tests use **fluent DSL** for readability:

```kotlin
mainTestScreen {
    fabSNI.performClick()
    
    noteScreen {
        textFieldSNI.performTextInput("Note content")
        saveNoteMenuButtonSNI.performClick()
        
        commonDialog {
            yesDialogButtonSNI.performClick()
        }
    }
}
```

This creates a hierarchical, readable test structure.

### Base Test Case (`BaseTestCase.kt`)

Base class for test cases providing screen access:

```kotlin
abstract class BaseTestCase(
    protected val composeTestRule: ComposeContentTestRule
) {
    protected fun mainTestScreen(block: MainTestScreen.() -> Unit) =
        MainTestScreen(composeTestRule).block()
    
    protected fun noteScreen(block: NoteScreen.() -> Unit) =
        NoteScreen(composeTestRule).block()
    
    // ... other screen accessors
}
```

### Test Utilities

#### Wait Extensions (`ext.kt`)

```kotlin
fun ComposeContentTestRule.waitUntilDisplayed(
    timeoutMillis: Long = 5000,
    blockSNI: () -> SemanticsNodeInteraction
) {
    waitUntil(timeoutMillis) {
        try {
            blockSNI().assertIsDisplayed()
            true
        } catch (e: AssertionError) {
            false
        }
    }
}
```

#### Database Test Utilities (`DbTestEncryptor.kt`)

Utilities for testing database encryption:

```kotlin
object DbTestEncryptor {
    fun encryptDatabase(password: String)
    fun decryptDatabase()
    fun isDatabaseEncrypted(): Boolean
}
```

#### UI Thread Utilities (`runOnUiThread.kt`)

Execute actions on UI thread:

```kotlin
fun runOnUiThread(block: () -> Unit) {
    // Platform-specific UI thread execution
}
```

## Platform Support

### Current Platforms

- ✅ **Android** - Via AndroidJUnitRunner + Compose Test
- ✅ **Desktop JVM** - Via Compose Desktop Test

### Future Expansion

- ⚠️ **iOS** - Planned (requires Compose iOS testing support)
- ⚠️ **Web** - Planned (requires Compose Web testing support)

## Usage in Platform Tests

### Android Tests (`app/android/src/androidTest/`)

```kotlin
@RunWith(AndroidJUnit4::class)
class AndroidUiTest : AbstractUiTests() {
    
    @get:Rule
    override val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    override fun pressBack() {
        Espresso.pressBack()
    }
    
    override fun closeSoftKeyboard() {
        Espresso.closeSoftKeyboard()
    }
    
    @Before
    override fun setUp() {
        // Setup
    }
    
    @After
    override fun tearDown() {
        // Teardown
    }
    
    @Test
    override fun crudNoteTest() = super.crudNoteTest()
    
    // ... other tests
}
```

### Desktop Tests (`app/desktop/src/jvmTest/`)

```kotlin
class DesktopUiTest : AbstractUiTests() {
    
    @get:Rule
    override val composeTestRule = createComposeRule()
    
    override fun pressBack() {
        // Desktop back action
    }
    
    override fun closeSoftKeyboard() {
        // No-op on desktop
    }
    
    @Before
    override fun setUp() {
        composeTestRule.setContent {
            App()
        }
    }
    
    @Test
    override fun crudNoteTest() = super.crudNoteTest()
}
```

## Dependencies

### Core
- `core:domain` - Domain models
- `core:data` - Data layer for test database
- `core:presentation` - ViewModels
- `core:test` - Test utilities
- `ui:shared` - UI under test

### Compose Testing
- `compose.desktop.uiTestJUnit4` - Compose testing framework
- `compose.desktop.currentOs` - Desktop runtime

### Testing Frameworks
- `androidx.lifecycle.common` - Lifecycle
- `androidx.lifecycle.runtime` - Runtime
- `koin.core` - Dependency injection
- `turbine` - Flow testing
- `napier` - Logging

## Writing New Tests

### Creating a New Test Case

```kotlin
class NewFeatureTestCase(
    composeTestRule: ComposeContentTestRule
) : () -> Unit, BaseTestCase(composeTestRule) {
    
    override fun invoke() = runTest {
        mainTestScreen {
            // Navigate to feature
            settingsButtonSNI.performClick()
            
            settingsScreen {
                // Test feature
                newFeatureSNI.performClick()
                
                // Assert result
                composeTestRule.waitUntilDisplayed(blockSNI = ::resultSNI)
                resultSNI.assertIsDisplayed()
            }
        }
    }
}
```

### Creating a New Screen Object

```kotlin
class NewScreen(private val composeTestRule: ComposeContentTestRule) {
    val titleSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithText("Screen Title")
    
    val buttonSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithContentDescription("Action Button")
    
    fun screen(block: NewScreen.() -> Unit) = apply(block)
}

// Add to BaseTestCase
protected fun newScreen(block: NewScreen.() -> Unit) =
    NewScreen(composeTestRule).block()
```

## AI Agent Guidelines

**See [CONTRIBUTING.md](../CONTRIBUTING.md) for general guidelines.**

### UI Test-Specific
- Follow Kaspresso-inspired patterns
- Use Page Object Model consistently
- Create reusable test case classes
- Write readable, hierarchical tests
- Use semantic properties for finding UI elements
- Always wait for elements before interacting

## Best Practices

### Finding UI Elements

```kotlin
// By text
val nodeSNI = composeTestRule.onNodeWithText("Click me")

// By content description (accessibility)
val nodeSNI = composeTestRule.onNodeWithContentDescription("Action button")

// By tag
val nodeSNI = composeTestRule.onNodeWithTag("unique_tag")

// By semantic property
val nodeSNI = composeTestRule.onNode(hasClickAction())
```

### Assertions

```kotlin
// Visibility
nodeSNI.assertIsDisplayed()
nodeSNI.assertDoesNotExist()

// Text
nodeSNI.assertTextEquals("Expected text")
nodeSNI.assertTextContains("partial text")

// State
nodeSNI.assertIsEnabled()
nodeSNI.assertIsNotEnabled()
nodeSNI.assertIsSelected()
```

### Actions

```kotlin
// Click
nodeSNI.performClick()

// Text input
nodeSNI.performTextInput("Input text")
nodeSNI.performTextClearance()

// Scroll
nodeSNI.performScrollTo()
```

## Running UI Tests

### Android

```bash
# Run all Android UI tests (requires emulator/device)
./gradlew :app:android:connectedCheck

# Run specific test
./gradlew :app:android:connectedCheck --tests "com.softartdev.notedelight.AndroidUiTest.crudNoteTest"
```

### Desktop

```bash
# Run all Desktop UI tests
./gradlew :app:desktop:jvmTest

# Run specific test
./gradlew :app:desktop:test --tests "com.softartdev.notedelight.DesktopUiTest.crudNoteTest"
```

## Troubleshooting

### Test Flakiness

Use wait utilities to prevent flakiness:

```kotlin
// Wait for element before interacting
composeTestRule.waitUntilDisplayed { fabSNI }
fabSNI.performClick()
```

### Element Not Found

Add semantic properties to UI elements:

```kotlin
// In UI code
Button(
    onClick = { },
    modifier = Modifier.semantics {
        contentDescription = "Create Note"
    }
)
```

## Related Modules

- **Used by**: `app:android` (androidTest), `app:desktop` (jvmTest)
- **Depends on**: `ui:shared`, `core:presentation`, `core:data`, `core:test`

## Resources

- [Kaspresso Library](https://github.com/KasperskyLab/Kaspresso)
- [Compose Testing](https://developer.android.com/jetpack/compose/testing)
- [Page Object Model](https://martinfowler.com/bliki/PageObject.html)

