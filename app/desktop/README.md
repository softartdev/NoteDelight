# Desktop App Module

## Overview

The `app:desktop` module is the **Desktop JVM application** for NoteDelight, built with Compose Multiplatform. It provides a native desktop experience on Windows, macOS, and Linux with full feature parity to the mobile apps.

## Purpose

- Provide desktop application entry point
- Configure platform-specific window settings
- Package desktop distributions (DMG, MSI, DEB, etc.)
- Support desktop-specific features (menu bar, system tray, file dialogs)
- Implement desktop UI tests

## Architecture

```
app:desktop (Desktop JVM Application)
    ├── src/
    │   ├── jvmMain/
    │   │   ├── kotlin/
    │   │   │   └── com/softartdev/notedelight/
    │   │   │       └── main.kt              # Application entry point
    │   │   └── resources/
    │   │       ├── icon.icns                # macOS icon
    │   │       ├── icon.ico                 # Windows icon
    │   │       └── icon.png                 # Linux icon
    │   └── jvmTest/
    │       └── kotlin/
    │           └── com/softartdev/notedelight/
    │               └── DesktopUiTest.kt     # Desktop UI tests
    ├── build.gradle.kts                     # Build & packaging config
    └── compose-desktop.pro                  # ProGuard rules for release
```

## Key Components

### main.kt

Application entry point using Compose Desktop:

```kotlin
fun main() = application {
    val windowState = rememberWindowState(
        width = 800.dp,
        height = 600.dp,
        placement = WindowPlacement.Floating
    )
    
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Note Delight",
        icon = painterResource("icon.png")
    ) {
        // Initialize Koin
        KoinApplication(application = {
            modules(desktopModule)
        }) {
            App() // Shared Compose UI
        }
    }
}
```

### Window Configuration

Desktop-specific window features:

```kotlin
Window(
    onCloseRequest = ::exitApplication,
    state = windowState,
    title = "Note Delight",
    icon = painterResource("icon.png"),
    undecorated = false,
    transparent = false,
    resizable = true,
    enabled = true,
    focusable = true,
    alwaysOnTop = false,
    onPreviewKeyEvent = { handleKeyEvent(it) },
    onKeyEvent = { handleKeyEvent(it) }
)
```

## Platform Support

### Supported Operating Systems

- ✅ **Windows** (x86_64, arm64)
  - Native Windows installer (MSI)
  - Portable executable

- ✅ **macOS** (x86_64, arm64/Apple Silicon)
  - Native DMG installer
  - App bundle (.app)
  - Mac App Store ready

- ✅ **Linux** (x86_64, arm64)
  - DEB package (Debian/Ubuntu)
  - RPM package (Fedora/RedHat)
  - AppImage (universal)

## Building & Packaging

### Development Build

```bash
# Run application in development mode
./gradlew :app:desktop:run
```

### Package Distributions

```bash
# Package for current OS
./gradlew :app:desktop:packageDistributionForCurrentOS

# Create DMG (macOS)
./gradlew :app:desktop:packageDmg

# Create MSI (Windows)
./gradlew :app:desktop:packageMsi

# Create DEB (Linux)
./gradlew :app:desktop:packageDeb

# Create all distributions
./gradlew :app:desktop:packageReleaseDistributionForCurrentOS
```

### Package Outputs

Distributions are created in:
```
app/desktop/build/compose/binaries/main/
├── dmg/          # macOS installer
├── msi/          # Windows installer
├── deb/          # Debian package
├── app/          # macOS app bundle
└── exe/          # Windows executable
```

## Desktop-Specific Features

### Menu Bar

Native menu bar support:

```kotlin
MenuBar {
    Menu("File") {
        Item("New Note", onClick = { /* ... */ })
        Item("Settings", onClick = { /* ... */ })
        Separator()
        Item("Exit", onClick = { exitApplication() })
    }
    Menu("Edit") {
        Item("Cut", onClick = { /* ... */ })
        Item("Copy", onClick = { /* ... */ })
        Item("Paste", onClick = { /* ... */ })
    }
}
```

### System Tray

System tray icon (optional feature):

```kotlin
Tray(
    icon = painterResource("icon.png"),
    tooltip = "Note Delight",
    onAction = { /* Show main window */ },
    menu = {
        Item("Open", onClick = { /* ... */ })
        Item("Exit", onClick = ::exitApplication)
    }
)
```

### File Dialogs

Native file picker dialogs:

```kotlin
val fileChooser = JFileChooser()
fileChooser.showOpenDialog(null)
```

### Keyboard Shortcuts

Desktop keyboard shortcuts:

```kotlin
onKeyEvent = { event ->
    when {
        // Ctrl+N / Cmd+N - New note
        event.isMetaPressed && event.key == Key.N -> {
            createNewNote()
            true
        }
        // Ctrl+S / Cmd+S - Save note
        event.isMetaPressed && event.key == Key.S -> {
            saveNote()
            true
        }
        else -> false
    }
}
```

## Configuration

### Application Metadata

Configured in `build.gradle.kts`:

```kotlin
compose.desktop {
    application {
        mainClass = "com.softartdev.notedelight.MainKt"
        
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "NoteDelight"
            packageVersion = "8.4.1"
            description = "Secure note-taking application"
            vendor = "softartdev"
            
            macOS {
                bundleID = "com.softartdev.notedelight"
                iconFile.set(project.file("src/jvmMain/resources/icon.icns"))
            }
            
            windows {
                iconFile.set(project.file("src/jvmMain/resources/icon.ico"))
                menuGroup = "NoteDelight"
                perUserInstall = true
            }
            
            linux {
                iconFile.set(project.file("src/jvmMain/resources/icon.png"))
            }
        }
    }
}
```

### ProGuard Configuration

Release builds use ProGuard for optimization:

`compose-desktop.pro`:
```proguard
-dontwarn **
-ignorewarnings

# Keep main class
-keep class com.softartdev.notedelight.MainKt { *; }

# Keep Compose
-keep class androidx.compose.** { *; }

# Keep Kotlin
-keep class kotlin.** { *; }
```

## Testing

### Desktop UI Tests

Located in `src/jvmTest/`:

```kotlin
class DesktopUiTest : AbstractUiTests() {
    
    @get:Rule
    override val composeTestRule = createComposeRule()
    
    override fun pressBack() {
        // Desktop back action
    }
    
    override fun closeSoftKeyboard() {
        // No keyboard on desktop
    }
    
    @Before
    override fun setUp() {
        // Initialize Koin
        startKoin {
            modules(desktopModule)
        }
        
        composeTestRule.setContent {
            App()
        }
    }
    
    @Test
    override fun crudNoteTest() = super.crudNoteTest()
    
    @Test
    override fun editTitleAfterCreateTest() = super.editTitleAfterCreateTest()
    
    // ... other tests
}
```

### Running Tests

```bash
# Run all desktop tests
./gradlew :app:desktop:jvmTest

# Run specific test
./gradlew :app:desktop:test --tests "DesktopUiTest.crudNoteTest"
```

## Dependencies

### Core
- `core:domain` - Domain layer
- `core:data` - Data layer (SQLDelight or Room)
- `core:presentation` - ViewModels
- `ui:shared` - Shared UI

### Compose Desktop
- `compose.desktop.currentOs` - Compose Desktop runtime
- `compose.desktop.uiTestJUnit4` - UI testing

### Dependency Injection
- `koin.core` - Koin DI

### Testing
- `ui:test-jvm` - UI test framework
- `core:test` - Test utilities

## Data Storage

Desktop app stores data in platform-specific directories:

- **Windows**: `%APPDATA%/NoteDelight/`
- **macOS**: `~/Library/Application Support/NoteDelight/`
- **Linux**: `~/.local/share/NoteDelight/`

Database file: `notes.db`

## Performance Optimization

### ProGuard/R8

Release builds are optimized:

```bash
./gradlew :app:desktop:packageReleaseDistributionForCurrentOS
```

This:
- Removes unused code
- Optimizes bytecode
- Obfuscates class names
- Reduces package size

### JVM Arguments

Optimize JVM performance:

```kotlin
compose.desktop.application {
    jvmArgs += listOf(
        "-Xmx512m",           // Max heap size
        "-XX:+UseG1GC",       // Use G1 garbage collector
        "-Dfile.encoding=UTF-8"
    )
}
```

## Distribution

### GitHub Releases

Automated via GitHub Actions:

```yaml
- name: Build Desktop Distributions
  run: ./gradlew :app:desktop:packageDistributionForCurrentOS
  
- name: Upload Release Assets
  uses: actions/upload-release-asset@v1
  with:
    asset_path: app/desktop/build/compose/binaries/main/dmg/NoteDelight-*.dmg
```

### Mac App Store

Requirements for Mac App Store:
1. Sign with Apple Developer certificate
2. Enable App Sandbox
3. Request necessary entitlements
4. Notarize the app
5. Submit via App Store Connect

### Microsoft Store

Requirements for Microsoft Store:
1. Convert MSI to MSIX
2. Sign with Microsoft certificate
3. Create Store listing
4. Submit for certification

## AI Agent Guidelines

**See [CONTRIBUTING.md](../CONTRIBUTING.md) for general guidelines.**

### Desktop-Specific
- Persist window size/position across restarts
- Follow platform UI guidelines
- Support standard desktop shortcuts
- Handle file paths correctly per platform
- Optimize for desktop (no battery concerns)
- Write desktop-specific UI tests

## Best Practices

### Window State Management

```kotlin
// Save and restore window position
val windowState = rememberWindowState(
    width = preferences.getInt("windowWidth", 800).dp,
    height = preferences.getInt("windowHeight", 600).dp
)

DisposableEffect(windowState) {
    onDispose {
        preferences.putInt("windowWidth", windowState.size.width.value.toInt())
        preferences.putInt("windowHeight", windowState.size.height.value.toInt())
    }
}
```

### Platform Detection

```kotlin
val osName = System.getProperty("os.name").lowercase()
val isMac = osName.contains("mac")
val isWindows = osName.contains("win")
val isLinux = osName.contains("nux")
```

### Native Dialogs

```kotlin
// Use Swing for native file dialogs
FileDialog(
    onCloseRequest = { result ->
        if (result != null) {
            // Handle selected file
        }
    }
)
```

## Troubleshooting

### Building Issues

1. **Java version**: Ensure JDK matches `libs.versions.toml`
2. **Clean build**: `./gradlew clean :app:desktop:build`
3. **Cache issues**: Delete `.gradle/` and rebuild

### Packaging Issues

1. **Icon not showing**: Check icon file paths and formats
2. **App won't launch**: Check `mainClass` in build.gradle
3. **Signing errors**: Verify certificates on macOS/Windows

### Performance Issues

1. **High memory usage**: Adjust JVM args
2. **Slow startup**: Use ProGuard for release builds
3. **UI lag**: Profile with VisualVM or JProfiler

## Related Modules

- **Depends on**: `ui:shared`, `core:presentation`, `core:data`, `core:domain`
- **Test dependencies**: `ui:test-jvm`, `core:test`
- **Alternative apps**: `app:android`, `app:web`, `app:iosApp`

## Resources

- [Compose Desktop Documentation](https://github.com/JetBrains/compose-jb)
- [Compose Desktop Tutorial](https://www.jetbrains.com/lp/compose-mpp/)
- [Packaging Desktop Apps](https://github.com/JetBrains/compose-jb/tree/master/tutorials/Native_distributions_and_local_execution)

