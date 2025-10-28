# Android App Module

## Overview

The `app:android` module is the **Android application entry point** for NoteDelight. It packages the shared Compose UI and provides Android-specific configuration, including Firebase integration, ProGuard rules, signing configuration, and platform-specific features.

## Purpose

- Provide Android app entry point (`MainActivity`)
- Configure Android-specific features (splash screen, notifications, etc.)
- Integrate Firebase (Analytics, Crashlytics)
- Setup ProGuard/R8 for release builds
- Configure signing for app distribution
- Implement Android instrumented UI tests
- Support fastlane for CI/CD deployment

## Architecture

```
app:android (Android Application)
    ├── src/
    │   ├── main/
    │   │   ├── kotlin/
    │   │   │   └── com/softartdev/notedelight/
    │   │   │       ├── MainActivity.kt           # Main activity
    │   │   │       ├── NoteDelightApp.kt        # Application class
    │   │   │       └── util/                     # Android utilities
    │   │   ├── AndroidManifest.xml
    │   │   ├── res/
    │   │   │   ├── mipmap/                       # App icons
    │   │   │   ├── values/                       # Themes, strings
    │   │   │   └── xml/                          # App config
    │   ├── androidTest/                          # UI tests (Espresso)
    │   │   └── kotlin/
    │   │       └── com/softartdev/notedelight/
    │   │           ├── AndroidUiTest.kt         # Main UI tests
    │   │           └── ...                       # More test files
    │   └── test/                                 # Unit tests
    │       └── kotlin/
    │           └── ExampleUnitTest.kt
    ├── build.gradle.kts                          # Build configuration
    ├── proguard-rules.pro                        # ProGuard rules
    ├── google-services.json                      # Firebase config
    ├── keystore.properties                       # Signing config (local)
    └── fastlane/                                 # CI/CD automation
        ├── Fastfile
        └── Appfile
```

## Key Components

### MainActivity.kt

Main activity using Jetpack Compose:

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App() // Shared Compose UI
        }
    }
}
```

### NoteDelightApp.kt

Application class for initialization:

```kotlin
class NoteDelightApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin DI
        startKoin {
            androidContext(this@NoteDelightApp)
            modules(appModule)
        }
        
        // Initialize Firebase
        FirebaseCrashlytics.getInstance()
        
        // Initialize LeakCanary (debug builds)
        if (BuildConfig.DEBUG) {
            LeakCanary.config = LeakCanary.config.copy(...)
        }
    }
}
```

## Build Configuration

### Variants

- **Debug**: Development builds with debugging enabled
  - No minification
  - No ProGuard
  - Crashlytics mapping disabled
  - LeakCanary enabled

- **Release**: Production builds for distribution
  - R8 minification enabled
  - ProGuard rules applied
  - Crashlytics mapping enabled
  - Signed with release keystore

### Version Information

```kotlin
android {
    defaultConfig {
        applicationId = "com.softartdev.noteroom"
        versionCode = 841
        versionName = "8.4.1"
        // ...
    }
}
```

### Build Features

- **Compose**: Jetpack Compose UI
- **BuildConfig**: Build configuration access
- **Core Library Desugaring**: Java 8+ API support on older Android versions

## ProGuard/R8 Configuration

`proguard-rules.pro` contains rules for:
- Keeping domain models
- SQLCipher/SQLDelight classes
- Koin reflection
- Kotlin coroutines
- Firebase
- AndroidX libraries

```proguard
# Keep domain models
-keep class com.softartdev.notedelight.model.** { *; }

# SQLDelight
-keep class com.squareup.sqldelight.** { *; }

# Koin
-keepnames class * extends org.koin.core.module.Module
```

## Firebase Integration

### Services

1. **Firebase Analytics**: User behavior tracking
2. **Firebase Crashlytics**: Crash reporting and analysis

### Configuration

- `google-services.json`: Firebase project configuration (included in repo with sanitized data)
- Gradle plugin: `com.google.gms.google-services`
- Crashlytics plugin: `com.google.firebase.crashlytics`

### Usage

```kotlin
// Log event
FirebaseAnalytics.getInstance(context).logEvent("note_created", null)

// Log crash
FirebaseCrashlytics.getInstance().recordException(exception)
```

## Signing Configuration

### Debug Signing

Uses default Android debug keystore.

### Release Signing

Configured via `keystore.properties` (not in VCS):

```properties
# keystore.properties (local only)
storeFile=/path/to/keystore.jks
storePassword=***
keyAlias=***
keyPassword=***
```

Applied via `gradle/common-android-sign-conf.gradle`:

```kotlin
apply(from = "$rootDir/gradle/common-android-sign-conf.gradle")
```

## Testing

### Unit Tests (`test/`)

Standard JUnit tests:

```bash
./gradlew :app:android:testDebugUnitTest
```

### Instrumented Tests (`androidTest/`)

UI tests using:
- **Compose Test** - Compose UI testing
- **Espresso** - Android UI testing framework
- **JUnit4** - Test runner
- **Test Orchestrator** - Isolated test execution

#### Running Instrumented Tests

```bash
# Requires connected device or emulator
./gradlew :app:android:connectedCheck
```

#### Test Configuration

```kotlin
android {
    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
        emulatorControl.enable = true
    }
}

dependencies {
    androidTestImplementation(project(":ui:test-jvm"))
    androidTestUtil(libs.androidx.test.orchestrator)
}
```

### Test Coverage

- ✅ CRUD operations
- ✅ Note editing
- ✅ Password management
- ✅ Database encryption
- ✅ Navigation flows

## Android-Specific Features

### Splash Screen

Uses Android 12+ Splash Screen API:

```xml
<style name="Theme.App.SplashScreen" parent="Theme.SplashScreen">
    <item name="windowSplashScreenBackground">@color/splash_background</item>
    <item name="windowSplashScreenAnimatedIcon">@drawable/ic_launcher</item>
</style>
```

### Localization

Supports multiple languages:
- English (default)
- Russian

Locale configuration generated automatically:

```kotlin
android {
    androidResources.generateLocaleConfig = true
    defaultConfig {
        androidResources.localeFilters += setOf("en", "ru")
    }
}
```

### LeakCanary Integration

Memory leak detection in debug builds:

```kotlin
dependencies {
    debugImplementation(libs.leakCanary.android)
    debugImplementation(libs.leakCanary.android.process)
    implementation(libs.leakCanary.plumber.android)
    androidTestImplementation(libs.leakCanary.android.instrumentation)
}
```

## Fastlane Integration

Automates app deployment to Google Play:

### Fastfile

```ruby
lane :beta do
  gradle(task: "bundle", build_type: "Release")
  upload_to_play_store(track: "beta")
end

lane :production do
  gradle(task: "bundle", build_type: "Release")
  upload_to_play_store(track: "production")
end
```

### Running Fastlane

```bash
cd app/android
fastlane beta    # Deploy to beta track
fastlane production  # Deploy to production
```

## Dependencies

### Core
- `core:domain` - Domain layer
- `core:data` - Data layer (SQLDelight or Room)
- `core:presentation` - ViewModels
- `ui:shared` - Shared UI

### Android
- `androidx.activity.compose` - Activity with Compose
- `androidx.navigation.compose` - Navigation
- `compose.ui` - Compose UI
- `compose.material3` - Material 3
- `koin.android` - DI for Android

### Firebase
- `firebase.analytics` - Analytics
- `firebase.crashlytics` - Crash reporting

### Debugging
- `leakcanary.android` - Memory leak detection

### Testing
- `ui:test-jvm` - UI test framework
- `androidx.test.ext.junit` - AndroidX Test
- `androidx.test.runner` - Test runner
- `androidx.test.orchestrator` - Test orchestrator
- `espresso.core` - UI testing
- `compose.uiTestJUnit4` - Compose testing

## Building

### Debug Build

```bash
# Build APK
./gradlew :app:android:assembleDebug

# Install on device
./gradlew :app:android:installDebug

# Build and install
adb install -r app/android/build/outputs/apk/debug/android-debug.apk
```

### Release Build

```bash
# Build signed APK
./gradlew :app:android:assembleRelease

# Build App Bundle (for Google Play)
./gradlew :app:android:bundleRelease
```

Outputs:
- APK: `app/android/build/outputs/apk/release/android-release.apk`
- Bundle: `app/android/build/outputs/bundle/release/android-release.aab`

## Running

```bash
# Run debug variant
./gradlew :app:android:installDebug
adb shell am start -n com.softartdev.noteroom/.MainActivity

# Or use Android Studio Run button
```

## Distribution

### Google Play Store

1. Build release bundle: `./gradlew :app:android:bundleRelease`
2. Sign with release keystore (automatic via gradle config)
3. Upload to Google Play Console
4. Or use Fastlane: `fastlane production`

### Direct APK Distribution

Build signed APK and distribute via GitHub Releases or other channels.

## Minimum Requirements

- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: Latest (defined in `libs.versions.toml`)
- **Compile SDK**: Latest (defined in `libs.versions.toml`)

## AI Agent Guidelines

**See [CONTRIBUTING.md](../CONTRIBUTING.md) for general guidelines.**

### Android-Specific
- Update Android manifest for new permissions/features
- Add ProGuard rules for new libraries
- Update versionCode and versionName for releases
- Never commit signing keys or passwords
- Keep `google-services.json` sanitized
- Write instrumented tests for Android-specific features

## Best Practices

### Activity Configuration

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            App()
        }
    }
}
```

### Permission Handling

```kotlin
// In manifest
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

// Runtime request
val permissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
) { isGranted ->
    // Handle permission result
}
```

## Troubleshooting

### Build Issues

1. **Clean build**: `./gradlew clean`
2. **Invalidate caches**: Android Studio → File → Invalidate Caches
3. **Update dependencies**: `./gradlew --refresh-dependencies`

### Signing Issues

1. Check `keystore.properties` exists and has correct values
2. Verify keystore file path is correct
3. Ensure passwords are correct

### Test Failures

1. Ensure emulator/device is running
2. Clear app data: `adb shell pm clear com.softartdev.noteroom`
3. Run with orchestrator: Already configured

## Related Modules

- **Depends on**: `ui:shared`, `core:presentation`, `core:data`, `core:domain`
- **Test dependencies**: `ui:test-jvm`, `core:test`
- **Alternative apps**: `app:desktop`, `app:web`, `app:iosApp`

## Resources

- [Android Developers](https://developer.android.com/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Firebase Documentation](https://firebase.google.com/docs)
- [Fastlane](https://docs.fastlane.tools/)
- [Google Play Console](https://play.google.com/console)

