# UI Test Module

## Overview

The `ui:test` module provides **common Compose Multiplatform UI tests** that can run on all supported platforms. This module follows the [official Compose Multiplatform testing documentation](https://kotlinlang.org/docs/multiplatform/compose-test.html).

## Purpose

- Provide multiplatform UI tests using Compose Multiplatform testing API
- Enable testing Compose UI across all platforms (Android, iOS, JVM Desktop, Web)
- Share test code across platforms using `commonTest` source set

## Running Tests

The `ui/test` module provides the base test framework. Platform-specific test implementations extend `CommonUiTests`:

### iOS (Simulator)
```bash
# Run iOS UI tests (extends CommonUiTests)
./gradlew :app:ios-kit:iosSimulatorArm64Test

# Run test framework tests
./gradlew :ui:test:iosSimulatorArm64Test
```
Note: the `ui:test` CocoaPods **release** framework for the iOS simulator is disabled because tests only need debug binaries.

### Android (Requires emulator/device)
```bash
# Run Android UI tests (uses AbstractJvmUiTests)
./gradlew :app:android:connectedCheck

# Run test framework tests
./gradlew :ui:test:connectedAndroidTest
```

### JVM (Desktop)
```bash
# Run Desktop UI tests (uses AbstractJvmUiTests)
./gradlew :app:desktop:jvmTest

# Run test framework tests
./gradlew :ui:test:jvmTest
```

### Web (wasmJs)
```bash
# Run Web UI tests (extends CommonUiTests, requires CHROME_BIN)
export CHROME_BIN=/path/to/chrome
./gradlew :app:web:wasmJsBrowserTest

# Run test framework tests
./gradlew :ui:test:wasmJsTest
```

## Module Structure

```
ui/test/
├── src/
│   ├── commonMain/              # Multiplatform test framework
│   │   └── kotlin/
│   │       └── com/softartdev/notedelight/
│   │           ├── AbstractUITests.kt      # Base test class
│   │           ├── CommonUiTests.kt       # Abstract base class for platform tests
│   │           ├── BaseTestCase.kt         # Base test case
│   │           ├── ui/
│   │           │   ├── cases/              # Reusable test cases
│   │           │   └── screen/             # Screen objects (Page Objects)
│   │           ├── di/                     # Test DI modules
│   │           └── runOnUiThread.kt         # Common UI thread interface
│   ├── androidMain/            # Android-specific implementations
│   │   └── kotlin/
│   │       └── runOnUiThread.android.kt
│   ├── jvmMain/                # JVM Desktop-specific implementations
│   │   └── kotlin/
│   │       └── runOnUiThread.jvm.kt
│   ├── iosMain/                # iOS-specific implementations
│   │   └── kotlin/
│   │       └── runOnUiThread.ios.kt
│   └── wasmJsMain/              # Web-specific implementations
│       └── kotlin/
│           └── runOnUiThread.wasmJs.kt
├── build.gradle.kts
└── README.md
```

**Note**: `CommonUiTests` is now in `commonMain` (not `commonTest`) as an abstract base class that platform-specific test classes extend:
- **iOS**: `app/ios-kit/src/commonTest/kotlin/IosUiTests.kt` extends `CommonUiTests`
- **Web**: `app/web/src/wasmJsTest/kotlin/WebUiTests.kt` extends `CommonUiTests`

## Dependencies

### Core
- `libs.compose.ui.test` - Compose Multiplatform testing API
- `libs.compose.material3` - Material 3 components for test UI
- `libs.compose.material.icons.extended` - Material icons
- `libs.compose.components.resources` - Compose resources
- `core:domain` - Domain models
- `core:presentation` - ViewModels
- `ui:shared` - UI under test

### Platform-Specific
- `libs.compose.desktop.jvm.<os>.<arch>` - Desktop platform support for JVM tests (selected via `composeDesktopCurrentOs` from build-logic)
- `androidx.compose.ui:ui-test-junit4-android` - Android instrumented test support

### Testing & Utilities
- `kotlin("test")` - Kotlin multiplatform testing
- `turbine` - Flow testing
- `koin.core` - Dependency injection
- `kermit` - Logging
- `material.theme.prefs` - Material theme preferences
- `androidx.lifecycle.runtime.compose` - Lifecycle-aware Compose
- `androidx.lifecycle.runtime.testing` - Testing utilities for lifecycle

## Test Architecture

### CommonUiTests Base Class

`CommonUiTests` is an abstract base class in `commonMain` that provides:
- Common test setup and teardown
- Database initialization and cleanup
- Koin dependency injection setup
- Compose UI test content setup
- Shared test methods that can be overridden by platform implementations

### Platform-Specific Implementations

Platform-specific test classes extend `CommonUiTests`:

- **iOS**: `app/ios-kit/src/commonTest/kotlin/IosUiTests.kt`
  - Runs on iOS Simulator
  - Handles iOS-specific database cleanup (WAL, journal files)
  
- **Web**: `app/web/src/wasmJsTest/kotlin/WebUiTests.kt`
  - Runs in headless Chrome via Karma
  - Uses SQL.js fallback for database operations
  - Requires `CHROME_BIN` environment variable

- **Android/Desktop**: Use `AbstractJvmUiTests` from `ui/test-jvm`
  - Bridges `ComposeContentTestRule` to `ComposeUiTest`
  - Platform-specific implementations in `app/android` and `app/desktop`

### Test Cases

Reusable test cases are defined in `ui/test/src/commonMain/kotlin/ui/cases/`:
- `CrudTestCase` - Create, read, update, delete operations
- `EditTitleAfterCreateTestCase` - Title editing after note creation
- `EditTitleAfterSaveTestCase` - Title editing after note save
- Additional test cases for encryption, password, locale, etc.

These test cases are shared across all platforms through the `CommonUiTests` base class.
