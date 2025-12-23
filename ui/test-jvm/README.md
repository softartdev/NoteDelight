# UI Test JVM Module

## Overview

The `ui:test-jvm` module provides **JVM-specific UI testing utilities** for Compose Multiplatform applications, specifically designed for **JVM-based platforms** (Android and Desktop). This module depends on `ui:test` which contains the multiplatform test framework with screen objects, test cases, and fluent DSL inspired by the Kaspresso library.

## Purpose

- Provide JVM-specific test utilities and abstractions
- Bridge between JVM-specific Compose Test APIs (`ComposeContentTestRule`) and multiplatform APIs (`ComposeUiTest`)
- Implement platform-specific `runOnUiThread` for JVM platforms
- Provide JVM-specific test setup and lifecycle management
- Enable Android and Desktop tests to use the multiplatform test framework from `ui:test`

## Architecture

```
ui:test-jvm (JVM-Specific UI Test Utilities)
    ├── src/
    │   └── main/
    │       └── kotlin/
    │           └── com/softartdev/notedelight/
    │               ├── ui/
    │               │   ├── AbstractJvmUiTests.kt      # JVM bridge to AbstractUITests
    │               │   └── AbstractNavigationTest.kt   # Navigation testing utilities
    │               ├── ComposeTestNodeProvider.kt     # JVM-specific test node provider
    │               └── (depends on ui:test for test cases, screen objects, etc.)
    └── build.gradle.kts

ui:test (Multiplatform UI Test Framework - Kaspresso-inspired)
    ├── src/
    │   ├── commonMain/
    │   │   └── kotlin/
    │   │       └── com/softartdev/notedelight/
    │   │           ├── ui/
    │   │           │   ├── AbstractUITests.kt         # Base test class (multiplatform)
    │   │           │   ├── BaseTestCase.kt            # Base test case
    │   │           │   ├── cases/                     # Reusable test cases
    │   │           │   └── screen/                    # Screen objects (Page Objects)
    │   │           ├── di/
    │   │           │   └── uiTestModules.kt           # Test DI modules
    │   │           ├── DbTestEncryptor.kt            # Database test utilities
    │   │           ├── UiThreadRouter.kt              # Test navigation
    │   │           └── ext.kt                         # Extension functions
    │   ├── jvmMain/
    │   │   └── kotlin/
    │   │       └── runOnUiThread.jvm.kt              # JVM UI thread implementation
    │   ├── androidMain/
    │   │   └── kotlin/
    │   │       └── runOnUiThread.android.kt           # Android UI thread implementation
    │   └── commonTest/
    │       └── kotlin/
    │           └── CommonUiTests.kt                  # Shared test implementations
    └── build.gradle.kts
```

## Relationship to ui:test Module

This module **depends on** `ui:test`, which contains the multiplatform test framework. The `ui:test` module provides:

- **Screen Objects**: Page Object Model for screens (in `commonMain`)
- **Test Cases**: Reusable test scenarios (in `commonMain`)
- **Fluent DSL**: Readable, expressive test syntax
- **Abstract Base**: `AbstractUITests` - multiplatform test base class
- **Platform Support**: Works on Android, iOS, JVM Desktop, and Web

This `ui:test-jvm` module provides:
- **JVM Bridge**: `AbstractJvmUiTests` - bridges JVM-specific `ComposeContentTestRule` to multiplatform `ComposeUiTest`
- **JVM Utilities**: Platform-specific test utilities for Android and Desktop
- **Compose Test Node Provider**: JVM-specific implementation for test node access

## Key Components

### Abstract JVM Test Base (`AbstractJvmUiTests.kt`)

JVM-specific bridge class that extends the multiplatform `AbstractUITests`:

```kotlin
abstract class AbstractJvmUiTests : AbstractUITests() {
    abstract val composeTestRule: ComposeContentTestRule
    override val composeUiTest: ComposeUiTest by lazy { reflect(composeTestRule) }
}
```

This class bridges the JVM-specific `ComposeContentTestRule` (from Compose Desktop/Android) to the multiplatform `ComposeUiTest` API used by `ui:test` module.

### Multiplatform Test Base (`AbstractUITests.kt` in `ui:test`)

The actual base class is in `ui:test` module:

```kotlin
abstract class AbstractUITests {
    abstract val composeUiTest: ComposeUiTest
    
    // Lifecycle
    open fun setUp() = Unit
    open fun tearDown() = Unit
    
    // Test cases (can be overridden per platform)
    open fun crudNoteTest() = CrudTestCase(composeUiTest).invoke()
    open fun editTitleAfterCreateTest() = EditTitleAfterCreateTestCase(composeUiTest).invoke()
    // ... more test cases
    
    // Platform-specific actions
    abstract fun pressBack()
    abstract fun closeSoftKeyboard()
}
```

### Screen Objects and Test Cases

Screen objects and test cases are now in the `ui:test` module (see [ui/test/README.md](../test/README.md)). They use the multiplatform `ComposeUiTest` API and can run on all platforms.

**Note**: Screen objects and test cases have been moved to `ui/test/src/commonMain/kotlin/` to enable multiplatform testing. This module (`ui:test-jvm`) provides JVM-specific utilities and bridges.

### Compose Test Node Provider (`ComposeTestNodeProvider.kt`)

JVM-specific utility for accessing test nodes:

```kotlin
object ComposeTestNodeProvider {
    fun getComposeUiTest(composeTestRule: ComposeContentTestRule): ComposeUiTest {
        // Reflection-based bridge to access ComposeUiTest from ComposeContentTestRule
    }
}
```

This enables the bridge between JVM-specific test APIs and the multiplatform test framework.

### Navigation Testing (`AbstractNavigationTest.kt`)

JVM-specific navigation testing utilities for verifying navigation behavior across platforms.

## Platform Support

This module provides JVM-specific utilities for:

- ✅ **Android** - Via AndroidJUnitRunner + Compose Test
- ✅ **Desktop JVM** - Via Compose Desktop Test

**Note**: The multiplatform test framework in `ui:test` supports all platforms (Android, iOS, JVM Desktop, Web). This module provides the JVM-specific bridge and utilities.

## Usage in Platform Tests

### Android Tests (`app/android/src/androidTest/`)

```kotlin
@RunWith(AndroidJUnit4::class)
class AndroidUiTest : AbstractJvmUiTests() {
    
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
        super.setUp()
        // Additional setup
    }
    
    @After
    override fun tearDown() {
        super.tearDown()
        // Additional teardown
    }
    
    @Test
    override fun crudNoteTest() = super.crudNoteTest()
    
    // ... other tests
}
```

### Desktop Tests (`app/desktop/src/jvmTest/`)

```kotlin
class DesktopUiTest : AbstractJvmUiTests() {
    
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
        super.setUp()
        composeTestRule.setContent {
            App()
        }
    }
    
    @Test
    override fun crudNoteTest() = super.crudNoteTest()
}
```

**Note**: Tests now extend `AbstractJvmUiTests` which bridges to the multiplatform `AbstractUITests` from `ui:test`.

## Dependencies

### Core
- `ui:test` - **Multiplatform test framework** (screen objects, test cases, base classes)
- `core:domain` - Domain models
- `core:data` - Data layer for test database
- `core:presentation` - ViewModels
- `core:test` - Test utilities
- `ui:shared` - UI under test

### Compose Testing
- `compose.desktop.uiTestJUnit4` - Compose testing framework for JVM
- `compose.desktop.currentOs` - Desktop runtime

### Testing Frameworks
- `androidx.lifecycle.common` - Lifecycle
- `androidx.lifecycle.runtime` - Runtime
- `androidx.lifecycle.runtime.compose` - Compose lifecycle
- `androidx.lifecycle.runtime.testing` - Testing lifecycle utilities
- `koin.core` - Dependency injection
- `turbine` - Flow testing
- `kermit` - Logging

## Writing New Tests

**Note**: New test cases and screen objects should be added to the `ui:test` module (in `commonMain`) to enable multiplatform testing. See [ui/test/README.md](../test/README.md) for details.

This module (`ui:test-jvm`) is primarily for JVM-specific utilities and bridges. If you need to add JVM-specific test utilities, add them here.

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

## Locale Testing

The module includes `LocaleTestCase` to verify locale switching works correctly across platforms:

```kotlin
@Test
override fun localeTest() = super.localeTest()
```

The test verifies:
- Setting locale via `LocaleInteractor`
- Localized strings are displayed correctly
- Language dialog displays all language options
- Locale switching between English, Russian

**Platform-specific behavior**:
- **Android**: Uses `AppCompatDelegate.setApplicationLocales()`
- **Desktop/JVM**: Uses `Locale.setDefault()`
- **Web**: Uses `window.__customLocale` (requires script in `index.html`)

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
- **Depends on**: `ui:test` (multiplatform test framework), `ui:shared`, `core:presentation`, `core:data`, `core:test`
- **Provides**: JVM-specific utilities and bridges for multiplatform tests

## Resources

- [Kaspresso Library](https://github.com/KasperskyLab/Kaspresso)
- [Compose Testing](https://developer.android.com/jetpack/compose/testing)
- [Page Object Model](https://martinfowler.com/bliki/PageObject.html)

