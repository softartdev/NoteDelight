# UI Test Module

## Overview

The `ui:test` module provides **common Compose Multiplatform UI tests** that can run on all supported platforms. This module follows the [official Compose Multiplatform testing documentation](https://kotlinlang.org/docs/multiplatform/compose-test.html).

## Purpose

- Provide multiplatform UI tests using Compose Multiplatform testing API
- Enable testing Compose UI across all platforms (Android, iOS, JVM Desktop, Web)
- Share test code across platforms using `commonTest` source set

## Running Tests

Tests can be run on different platforms using the following Gradle commands:

### iOS (Simulator)
```bash
./gradlew :ui:test:iosSimulatorArm64Test
```

### Android (Requires emulator/device)
```bash
./gradlew :ui:test:connectedAndroidTest
```

### JVM (Desktop)
```bash
./gradlew :ui:test:jvmTest
```

### Web (wasmJs)
```bash
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
│   │           ├── BaseTestCase.kt         # Base test case
│   │           ├── ui/
│   │           │   ├── cases/              # Reusable test cases
│   │           │   └── screen/             # Screen objects (Page Objects)
│   │           ├── di/                     # Test DI modules
│   │           └── runOnUiThread.kt         # Common UI thread interface
│   ├── commonTest/             # Shared test implementations
│   │   └── kotlin/
│   │       └── com/softartdev/notedelight/
│   │           ├── CommonUiTests.kt
│   │           └── ExampleTest.kt
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

## Dependencies

### Core
- `compose.uiTest` - Compose Multiplatform testing API
- `compose.material3` - Material 3 components for test UI
- `compose.materialIconsExtended` - Material icons
- `compose.components.resources` - Compose resources
- `core:domain` - Domain models
- `core:presentation` - ViewModels
- `ui:shared` - UI under test

### Platform-Specific
- `compose.desktop.currentOs` - Desktop platform support for JVM tests
- `androidx.compose.ui:ui-test-junit4-android` - Android instrumented test support

### Testing & Utilities
- `kotlin("test")` - Kotlin multiplatform testing
- `turbine` - Flow testing
- `koin.core` - Dependency injection
- `kermit` - Logging
- `material.theme.prefs` - Material theme preferences
