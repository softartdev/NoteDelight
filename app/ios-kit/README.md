# iOS Kit Module

## Overview

The `app:ios-kit` module is a **Kotlin Multiplatform framework** that packages the shared code for iOS. It's distributed as a **CocoaPods pod** and consumed by the iOS app (`app:iosApp`). This module acts as a bridge between Kotlin/Native code and Swift/SwiftUI.

## Purpose

- Package Kotlin Multiplatform code for iOS consumption
- Generate CocoaPods framework specification
- Expose Kotlin APIs to Swift
- Handle iOS-specific initialization
- Provide iOS bindings for shared UI

## Architecture

```
app:ios-kit (iOS Framework - CocoaPods Pod)
    ├── src/
    │   └── commonMain/
    │       └── kotlin/
    │           └── com/softartdev/notedelight/
    │               └── IosApp.kt           # iOS app initialization
    ├── build.gradle.kts                    # Framework configuration
    ├── iosComposePod.podspec              # Generated podspec
    └── shared.podspec                      # Alternative podspec
```

## Key Components

### IosApp.kt

iOS application initialization helper:

```kotlin
object IosApp {
    fun initialize() {
        // Initialize Koin for iOS
        startKoin {
            modules(iosModule)
        }
    }
    
    @Composable
    fun ComposeApp() {
        App() // Shared Compose UI
    }
}
```

This provides a clean entry point for Swift code.

## CocoaPods Integration

### Framework Configuration

Configured in `build.gradle.kts`:

```kotlin
kotlin {
    cocoapods {
        summary = "Shared library for the NoteDelight app"
        homepage = "https://github.com/softartdev/NoteDelight"
        version = "1.0"
        ios.deploymentTarget = "14.0"
        
        // SQLCipher dependency
        pod("SQLCipher", libs.versions.iosSqlCipher.get(), linkOnly = true)
        
        framework {
            baseName = "iosComposePod"
            isStatic = false
        }
        
        // Disable podspec generation on non-macOS platforms
        if (!OperatingSystem.current().isMacOsX) noPodspec()
    }
}
```

### Generated Podspec

The build generates `iosComposePod.podspec`:

```ruby
Pod::Spec.new do |spec|
    spec.name                     = 'iosComposePod'
    spec.version                  = '1.0'
    spec.homepage                 = 'https://github.com/softartdev/NoteDelight'
    spec.source                   = { :http=> ''}
    spec.authors                  = ''
    spec.license                  = ''
    spec.summary                  = 'Shared library for the NoteDelight app'
    spec.vendored_frameworks      = 'build/cocoapods/framework/iosComposePod.framework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target = '14.0'
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':app:ios-kit',
        'PRODUCT_MODULE_NAME' => 'iosComposePod',
    }
                
    spec.script_phases = [
        {
            :name => 'Build iosComposePod',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                if [ "YES" = "$OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" ]; then
                  echo "Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED environment variable set to \"YES\""
                  exit 0
                fi
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$REPO_ROOT/../gradlew" -p "$REPO_ROOT" $KOTLIN_PROJECT_PATH:syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
            SCRIPT
        }
    ]
                
    spec.dependency 'SQLCipher', '~> 4.5.5'
end
```

### Podspec Generation

Generate or update podspec:

```bash
# Generate podspec
./gradlew :app:ios-kit:podspec

# Or let CocoaPods generate it automatically during pod install
```

## Framework Output

The build produces:

```
build/cocoapods/framework/
└── iosComposePod.framework/
    ├── iosComposePod           # Binary
    ├── Headers/
    │   └── iosComposePod.h     # Objective-C header
    ├── Modules/
    │   └── module.modulemap    # Module map
    └── Info.plist              # Framework metadata
```

## Platform Support

- ✅ **iOS** (arm64 - physical devices)
- ✅ **iOS Simulator** (arm64 - Apple Silicon Macs, x86_64 - Intel Macs)

### Target Configuration

```kotlin
kotlin {
    iosArm64()           // Physical iOS devices
    iosSimulatorArm64()  // iOS Simulator on Apple Silicon
    // iosX64()          // iOS Simulator on Intel (can be added if needed)
}
```

## Swift Interoperability

### Exposing Kotlin to Swift

Kotlin classes/functions are automatically exposed to Swift:

```kotlin
// Kotlin
class NoteManager {
    fun createNote(title: String): Note {
        // ...
    }
}

// Accessible in Swift as:
// let manager = NoteManager()
// let note = manager.createNote(title: "My Note")
```

### Name Mapping

- Kotlin `fun` → Swift `func`
- Kotlin `class` → Swift `class` (not `struct`)
- Kotlin `object` → Swift singleton class
- Kotlin `suspend fun` → Swift `async` function (or callback)

### Nullability

Kotlin nullability is preserved in Swift:

```kotlin
// Kotlin
fun getName(): String? { /* ... */ }

// Swift
func getName() -> String? { /* ... */ }
```

## Dependencies

### Kotlin Dependencies
- `core:domain` - Domain layer
- `core:data:db-sqldelight` - Data layer
- `core:presentation` - ViewModels
- `ui:shared` - Shared Compose UI

### iOS Dependencies
- `compose.ui` - Compose Multiplatform
- `compose.runtime` - Compose runtime
- `compose.foundation` - Foundation components
- `compose.material3` - Material 3

### CocoaPods Dependencies
- `SQLCipher` (~> 4.5.5) - Database encryption

## Building

### Build Framework

```bash
# Build for all iOS targets
./gradlew :app:ios-kit:build

# Build for specific target
./gradlew :app:ios-kit:linkDebugFrameworkIosArm64
./gradlew :app:ios-kit:linkDebugFrameworkIosSimulatorArm64
```

### Sync with Xcode

```bash
# Sync framework for use in Xcode
./gradlew :app:ios-kit:syncFramework \
    -Pkotlin.native.cocoapods.platform=iphonesimulator \
    -Pkotlin.native.cocoapods.archs=arm64
```

This is usually called automatically by CocoaPods during Xcode build.

## Integration with iOS App

### Podfile Configuration

In `app/iosApp/Podfile`:

```ruby
target 'iosApp' do
  use_frameworks!
  platform :ios, '14.0'
  
  # Local pod from ios-kit module
  pod 'iosComposePod', :path => '../ios-kit'
end
```

### Install Pod

```bash
cd app/iosApp
pod install
```

This creates `iosApp.xcworkspace` which includes the framework.

### Using in Swift

```swift
import iosComposePod
import SwiftUI

@main
struct NoteDelightApp: App {
    init() {
        // Initialize Kotlin
        IosApp.shared.initialize()
    }
    
    var body: some Scene {
        WindowGroup {
            ComposeViewController()
        }
    }
}

// Wrap Compose UI in UIViewController
struct ComposeViewController: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return ComposeControllerKt.ComposeController()
    }
    
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // Update if needed
    }
}
```

## Compose Multiplatform for iOS

### Compose UI Integration

The framework includes Compose Multiplatform UI:

```kotlin
// Exposed to Swift/UIKit
fun ComposeController(): UIViewController {
    return ComposeUIViewController {
        App() // Full Compose UI
    }
}
```

### UIKit Bridge

Compose UI runs in a `UIViewController`:

- Renders using Skia graphics
- Handles touch events
- Integrates with iOS lifecycle
- Supports iOS navigation

## Memory Management

### Kotlin/Native Memory Model

- Uses new Kotlin/Native memory model
- Objects can be shared across threads
- Automatic reference counting

### Stately Libraries

Uses Stately for thread-safe state management:

```kotlin
dependencies {
    implementation(libs.stately.common)
    implementation(libs.stately.isolate)      // iOS-specific
    implementation(libs.stately.iso.collections)  // iOS-specific
}
```

## Debugging

### Xcode Debugging

1. Build framework: `./gradlew :app:ios-kit:build`
2. Open `iosApp.xcworkspace` in Xcode
3. Set breakpoints in Swift code
4. Run app in simulator or device

### Kotlin Debugging

Kotlin code can be debugged from Xcode with proper symbol mapping.

## Testing

### UI Tests

The iOS kit includes multiplatform Compose UI tests that extend `CommonUiTests` from the `ui/test` module:

**Location**: `app/ios-kit/src/commonTest/kotlin/IosUiTests.kt`

**Test Coverage**:
- CRUD operations
- Title editing after create/save
- Database prepopulation
- Encryption flow
- Password settings
- Locale switching

**Running Tests**:

```bash
# Requires iOS Simulator to be running
./gradlew :app:ios-kit:iosSimulatorArm64Test
```

**Test Configuration**:
- Tests run on iOS Simulator (arm64)
- Uses Compose Multiplatform testing API
- Database is automatically cleaned up before each test
- Handles iOS-specific database file cleanup (WAL, journal files)

**Database Management**:
Tests use improved database deletion that properly handles:
- Main database file (`notes.db`)
- Write-Ahead Logging files (`notes.db-wal`)
- Shared memory files (`notes.db-shm`)
- Journal files (`notes.db-journal`)

This ensures clean test state and prevents test interference.

## Updating Framework

After changing Kotlin code:

```bash
# Rebuild framework
./gradlew :app:ios-kit:build

# Reinstall pod
cd app/iosApp
pod install

# Or use pod update
pod update iosComposePod
```

Xcode will automatically rebuild the framework during build.

## CI/CD

### Building in CI

```yaml
# GitHub Actions
- name: Build iOS Framework
  run: ./gradlew :app:ios-kit:linkReleaseFrameworkIosArm64
  
- name: Build iOS Simulator Framework
  run: ./gradlew :app:ios-kit:linkReleaseFrameworkIosSimulatorArm64
```

### Creating XCFramework

For distribution, create an XCFramework:

```bash
# Build for all architectures
./gradlew :app:ios-kit:linkReleaseFrameworkIosArm64
./gradlew :app:ios-kit:linkReleaseFrameworkIosSimulatorArm64

# Create XCFramework
xcodebuild -create-xcframework \
    -framework build/bin/iosArm64/releaseFramework/iosComposePod.framework \
    -framework build/bin/iosSimulatorArm64/releaseFramework/iosComposePod.framework \
    -output iosComposePod.xcframework
```

## AI Agent Guidelines

When working with this module:

1. **Swift compatibility**: Keep APIs Swift-friendly
2. **Nullability**: Use nullable types appropriately for Swift interop
3. **Naming**: Use clear, descriptive names (they appear in Swift)
4. **Memory**: Be aware of reference cycles
5. **Threads**: Use Stately for thread-safe state
6. **Testing**: Test on both simulator and device
7. **Podspec**: Regenerate after dependency changes
8. **Documentation**: Document public APIs (appears in Xcode)
9. **CocoaPods**: Keep CocoaPods configuration up to date
10. **Versioning**: Update version when making breaking changes

## Best Practices

### API Design for Swift

```kotlin
// Good: Swift-friendly API
class NoteManager {
    fun createNote(title: String, text: String): Note
    suspend fun saveNote(note: Note): Result<Unit>
}

// Avoid: Generic types, complex hierarchies
class Manager<T> {  // Generics don't translate well
    fun process(item: T): T
}
```

### Error Handling

```kotlin
// Use sealed classes for errors (maps to Swift enums)
sealed class Result<T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Error<T>(val message: String) : Result<T>()
}
```

### Async Operations

```kotlin
// Suspend functions become async in Swift
suspend fun loadNotes(): List<Note> {
    // ...
}

// Swift:
// Task {
//     let notes = try await loadNotes()
// }
```

## Troubleshooting

### Pod Install Failures

1. **Clean CocoaPods cache**: `pod cache clean --all`
2. **Deintegrate and reinstall**: `pod deintegrate && pod install`
3. **Check Xcode path**: `xcode-select -p`

### Build Errors

1. **Framework not found**: Run `./gradlew :app:ios-kit:build`
2. **Symbol conflicts**: Check for duplicate dependencies
3. **Architecture mismatch**: Verify target architectures

### Runtime Crashes

1. **Missing initialization**: Ensure `IosApp.initialize()` is called
2. **Thread issues**: Use Stately for shared state
3. **Memory issues**: Check for retain cycles

## Related Modules

- **Packages**: `ui:shared`, `core:presentation`, `core:data`, `core:domain`
- **Consumed by**: `app:iosApp`
- **Alternative**: Direct framework embedding (without CocoaPods)

## Resources

- [Kotlin/Native CocoaPods](https://kotlinlang.org/docs/native-cocoapods.html)
- [Compose Multiplatform iOS](https://www.jetbrains.com/lp/compose-mpp/)
- [Swift-Kotlin Interoperability](https://kotlinlang.org/docs/native-objc-interop.html)
- [CocoaPods Documentation](https://guides.cocoapods.org/)

