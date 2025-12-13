# Desktop App Module

## Overview

The `app:desktop` module is the **Desktop JVM application** for NoteDelight, built with Compose Multiplatform. It provides a native desktop experience on Windows, macOS, and Linux with full feature parity to the mobile apps, including database encryption support via SQLCipher.

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
    ├── keystore.properties                  # Signing config (local, not in VCS)
    └── compose-desktop.pro                  # ProGuard rules for release
```

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

## Configuration

### Application Metadata

Configured in `build.gradle.kts`.

### macOS Signing Configuration

macOS signing is configured via `gradle/common-desktop-mac-sign-conf.gradle` (similar to Android's `common-android-sign-conf.gradle`) and `app/desktop/keystore.properties` (local file, not in VCS).

See `app/desktop/keystore.properties` for the stub template. For CI/CD, the certificate is imported via `.github/scripts/import_macos_dev_id_cert.sh`.

## Testing

### Desktop UI Tests

Located in `src/jvmTest/`. The desktop UI tests include full encryption test coverage (setting password, flow after encryption, etc.).

### Running Tests

```bash
# Run all desktop tests
./gradlew :app:desktop:jvmTest

# Run specific test
./gradlew :app:desktop:test --tests "DesktopUiTest.crudNoteTest"
```

## Distribution

### GitHub Releases

Automated via `.github/workflows/desktop.yaml`. macOS builds are signed and notarized. Release artifacts include DMG, MSI, DEB, and JAR files.

### Mac App Store

Requirements for Mac App Store:
1. Sign with Apple Developer certificate (Developer ID Application)
2. Enable App Sandbox
3. Request necessary entitlements
4. Notarize the app (automated in CI/CD)
5. Submit via App Store Connect

**Note**: The current setup uses Developer ID Application certificates for distribution outside the Mac App Store. For Mac App Store distribution, you'll need App Store certificates instead.

## AI Agent Guidelines

**See [CONTRIBUTING.md](../CONTRIBUTING.md) for general guidelines.**

## Resources

- [Compose Desktop Documentation](https://github.com/JetBrains/compose-jb)
- [Compose Desktop Tutorial](https://www.jetbrains.com/lp/compose-mpp/)
- [Packaging Desktop Apps](https://github.com/JetBrains/compose-jb/tree/master/tutorials/Native_distributions_and_local_execution)

