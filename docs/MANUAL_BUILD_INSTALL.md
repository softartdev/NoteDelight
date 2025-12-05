# Manual Build and Install Guide

Quick reference for installing pre-built releases and building NoteDelight from source.

## Installing Pre-built Releases

Download pre-built releases from [GitHub Releases](https://github.com/softartdev/NoteDelight/releases).

### Desktop - JAR

**Download:** `Note Delight-<os>-<arch>-<version>.jar` from the release assets

**Install and Run:**
```bash
# Run JAR directly
java -jar "Note Delight-<os>-<arch>-<version>.jar"
```

**Requirements:** Java Runtime Environment (JRE) or JDK installed

### Desktop - macOS (DMG)

**Download:** `Note Delight-<version>.dmg` from the release assets

**Install:**
1. Double-click the DMG file
2. Drag "Note Delight" to the Applications folder
3. Eject the DMG
4. Launch from Applications

**First launch (if not signed):**
- Right-click the app → Open
- Click "Open" in the security dialog

### Desktop - Linux (DEB)

**Download:** `Note Delight-<version>.deb` from the release assets

**Install:**
```bash
sudo dpkg -i "Note Delight-<version>.deb"
sudo apt-get install -f  # Install dependencies if needed
```

**Launch:**
```bash
notedelight
```
Or find "Note Delight" in your application menu.

### Desktop - Windows (MSI)

**Download:** `Note Delight-<version>.msi` from the release assets

**Install:**
1. Double-click the MSI file
2. Follow the installation wizard
3. Launch from Start menu

## Building from Source

### Desktop

#### Run

```bash
# Development run
./gradlew :app:desktop:run
```

#### Build JAR

```bash
# Debug JAR
./gradlew :app:desktop:packageUberJarForCurrentOS

# Release JAR
./gradlew :app:desktop:packageReleaseUberJarForCurrentOS
```

**Output location:** `app/desktop/build/compose/jars/`

**JAR filename format:** `Note Delight-<os>-<arch>-<version>.jar`  
Example: `Note Delight-macos-arm64-8.4.607.jar`

**Run JAR:**
```bash
java -jar app/desktop/build/compose/jars/Note\ Delight-*.jar
```

#### Build Packages

```bash
# Debug packages
./gradlew :app:desktop:packageDistributionForCurrentOS
./gradlew :app:desktop:packageDmg      # macOS
./gradlew :app:desktop:packageMsi      # Windows
./gradlew :app:desktop:packageDeb      # Linux

# Release packages
./gradlew :app:desktop:packageReleaseDistributionForCurrentOS
./gradlew :app:desktop:packageReleaseDmg      # macOS
./gradlew :app:desktop:packageReleaseMsi     # Windows
./gradlew :app:desktop:packageReleaseDeb     # Linux
```

**Output locations:**
- Debug: `app/desktop/build/compose/binaries/main/`
  - DMG: `app/desktop/build/compose/binaries/main/dmg/Note Delight-<version>.dmg`
  - MSI: `app/desktop/build/compose/binaries/main/msi/Note Delight-<version>.msi`
  - DEB: `app/desktop/build/compose/binaries/main/deb/Note Delight-<version>.deb`
- Release: `app/desktop/build/compose/binaries/main-release/`
  - DMG: `app/desktop/build/compose/binaries/main-release/dmg/Note Delight-<version>.dmg`
  - MSI: `app/desktop/build/compose/binaries/main-release/msi/Note Delight-<version>.msi`
  - DEB: `app/desktop/build/compose/binaries/main-release/deb/Note Delight-<version>.deb`

### Android

#### Build

```bash
# Debug APK
./gradlew :app:android:assembleDebug

# Release APK (requires keystore.properties)
./gradlew :app:android:assembleRelease

# App Bundle for Play Store
./gradlew :app:android:bundleRelease
```

**Output locations:**
- Debug: `app/android/build/outputs/apk/debug/android-debug.apk`
- Release: `app/android/build/outputs/apk/release/android-release.apk`
- Bundle: `app/android/build/outputs/bundle/release/android-release.aab`

#### Install

```bash
# Install via Gradle
./gradlew :app:android:installDebug

# Install via ADB
adb install -r app/android/build/outputs/apk/debug/android-debug.apk

# Launch app
adb shell am start -n com.softartdev.noteroom/.MainActivity
```

**Note:** For release builds, create `app/android/keystore.properties` with signing configuration.

### iOS

#### Setup

```bash
# Install CocoaPods (if needed)
sudo gem install cocoapods

# Install dependencies
cd app/iosApp
pod install

# Regenerate podspec (if needed)
./gradlew :app:ios-kit:podspec
```

#### Build

```bash
# Open workspace in Xcode
open app/iosApp/iosApp.xcworkspace
```

Then in Xcode:
- Select device/simulator
- Press `Cmd + B` to build
- Press `Cmd + R` to run

**Note:** Always open `.xcworkspace`, not `.xcodeproj`

#### Command Line Build

```bash
# Pre-build: Link Kotlin framework
./gradlew :app:ios-kit:linkPodReleaseFrameworkIosArm64

# Build in Xcode command line
cd app/iosApp
xcodebuild -workspace iosApp.xcworkspace \
           -scheme iosApp \
           -configuration Debug \
           -sdk iphonesimulator \
           -destination 'platform=iOS Simulator,name=iPhone 15' \
           build
```

### Web

#### Development

```bash
# Development server with hot reload
./gradlew :app:web:wasmJsBrowserDevelopmentRun --continuous
```

#### Production Build

```bash
# Build production bundle
./gradlew :app:web:wasmJsBrowserProductionWebpack

# Alternative: Build and create distribution
./gradlew :app:web:wasmJsBrowserDistribution
```

**Output location:** `app/web/build/dist/wasmJs/productionExecutable/`

**Preview locally:**
```bash
cd app/web/build/dist/wasmJs/productionExecutable
python3 -m http.server 8000
# Open http://localhost:8000
```

## Quick Build (Skip iOS)

```bash
# Faster build excluding iOS tasks
./gradle/build_quick.sh
```

## Clean Build

```bash
./gradlew clean
./gradlew build
```
