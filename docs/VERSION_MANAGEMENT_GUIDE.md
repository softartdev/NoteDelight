# Version Management Guide for AI Agents

This guide provides comprehensive instructions for AI agents on how to update versions across all platforms and trigger CI/CD workflows in the NoteDelight project.

## Overview

The NoteDelight project supports multiple platforms with different versioning schemes:
- **Android**: Uses `versionCode` (integer) and `versionName` (semantic version)
- **Desktop**: Uses `packageVersion` (semantic version)
- **iOS**: Uses `MARKETING_VERSION` and `CFBundleShortVersionString` (semantic version)

## Version Update Process

### 1. Android Version Update

**File**: `app/android/build.gradle.kts`

**Current pattern** (based on git changes):
```kotlin
android {
    defaultConfig {
        versionCode = 842        // Increment by 1
        versionName = "8.4.2"    // Semantic version
    }
}
```

**Steps to update**:
1. Increment `versionCode` by 1 (integer)
2. Update `versionName` following semantic versioning (MAJOR.MINOR.PATCH)
3. Ensure both values are consistent

**Example update**:
```kotlin
// Before
versionCode = 841
versionName = "8.4.1"

// After
versionCode = 842
versionName = "8.4.2"
```

### 2. Desktop Version Update

**File**: `app/desktop/build.gradle.kts`

**Current pattern**:
```kotlin
compose.desktop {
    nativeDistributions {
        packageVersion = "4.0.0"  // Semantic version
    }
}
```

**Steps to update**:
1. Update `packageVersion` following semantic versioning
2. This version is used for packaging (DMG, MSI, DEB)

**Example update**:
```kotlin
// Before
packageVersion = "3.0.0"

// After
packageVersion = "4.0.0"
```

### 3. iOS Version Update

**Files to update**:
- `app/iosApp/iosApp.xcodeproj/project.pbxproj`
- `app/iosApp/iosApp/Info.plist`

**Current pattern**:
```xml
<!-- project.pbxproj -->
MARKETING_VERSION = 4.0;

<!-- Info.plist -->
<key>CFBundleShortVersionString</key>
<string>4.0</string>
```

**Steps to update**:
1. Update `MARKETING_VERSION` in `project.pbxproj` (appears in both Debug and Release configurations)
2. Update `CFBundleShortVersionString` in `Info.plist`
3. Ensure both values match

**Example update**:
```xml
<!-- project.pbxproj -->
// Before
MARKETING_VERSION = 3.0;

// After
MARKETING_VERSION = 4.0;

<!-- Info.plist -->
// Before
<key>CFBundleShortVersionString</key>
<string>3.0</string>

// After
<key>CFBundleShortVersionString</key>
<string>4.0</string>
```

## Quick Reference

### Files to Update
- `app/android/build.gradle.kts` - Android version
- `app/desktop/build.gradle.kts` - Desktop version
- `app/iosApp/iosApp.xcodeproj/project.pbxproj` - iOS project version
- `app/iosApp/iosApp/Info.plist` - iOS bundle version
- `CHANGELOG.md` - Release notes and change history
