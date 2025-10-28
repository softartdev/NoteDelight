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

## CI/CD Workflow Triggers

The project uses GitHub Actions workflows that are triggered by specific git tags:

### 1. Android Workflow

**File**: `.github/workflows/android.yml`
**Trigger**: Push tags matching `android/*`

**Commands to trigger**:
```bash
# Create and push Android release tag
git tag android/8.4.2
git push origin android/8.4.2
```

**What it does**:
- Builds Android app
- Runs unit tests
- Publishes to Google Play Store via Fastlane
- Archives build artifacts

### 2. Desktop Workflow

**File**: `.github/workflows/desktop.yaml`
**Trigger**: Push tags matching `desktop/*`

**Commands to trigger**:
```bash
# Create and push Desktop release tag
git tag desktop/4.0.0
git push origin desktop/4.0.0
```

**What it does**:
- Builds desktop app for multiple platforms (Ubuntu, macOS, Windows)
- Creates distribution packages (DMG, MSI, DEB)
- Creates GitHub release with artifacts

### 3. iOS Workflow

**File**: `.github/workflows/ios.yml`
**Trigger**: Push tags matching `ios/*`

**Commands to trigger**:
```bash
# Create and push iOS release tag
git tag ios/4.0.0
git push origin ios/4.0.0
```

**What it does**:
- Builds iOS app
- Runs tests
- Publishes to App Store via Fastlane
- Archives build artifacts and logs

## Complete Version Update Workflow

### Step 1: Update Version Numbers

Update versions in all platform files:

1. **Android** (`app/android/build.gradle.kts`):
   ```kotlin
   versionCode = 843
   versionName = "8.4.3"
   ```

2. **Desktop** (`app/desktop/build.gradle.kts`):
   ```kotlin
   packageVersion = "4.0.1"
   ```

3. **iOS** (`app/iosApp/iosApp.xcodeproj/project.pbxproj` and `app/iosApp/iosApp/Info.plist`):
   ```xml
   MARKETING_VERSION = 4.0.1;
   CFBundleShortVersionString = "4.0.1"
   ```

### Step 2: Commit Changes

```bash
git add app/android/build.gradle.kts app/desktop/build.gradle.kts app/iosApp/iosApp.xcodeproj/project.pbxproj app/iosApp/iosApp/Info.plist
git commit -m "chore: bump version to 8.4.3 (Android), 4.0.1 (Desktop/iOS)"
```

### Step 3: Create and Push Tags

```bash
# Create tags for each platform
git tag android/8.4.3
git tag desktop/4.0.1
git tag ios/4.0.1

# Push all tags
git push origin android/8.4.3
git push origin desktop/4.0.1
git push origin ios/4.0.1
```

## Version Numbering Guidelines

### Semantic Versioning (SemVer)

Follow the format: `MAJOR.MINOR.PATCH`

- **MAJOR**: Breaking changes, major feature additions
- **MINOR**: New features, backward compatible
- **PATCH**: Bug fixes, backward compatible

### Platform-Specific Considerations

#### Android
- `versionCode`: Always increment (integer)
- `versionName`: Follow SemVer, but can be more flexible
- Google Play requires `versionCode` to be higher than previous uploads

#### Desktop
- `packageVersion`: Follow SemVer strictly
- Used for package managers and distribution

#### iOS
- `MARKETING_VERSION`: Follow SemVer
- `CFBundleVersion`: Build number (can be different from marketing version)
- App Store requires version to be higher than previous submissions

## Automated Version Update Script

For AI agents, here's a template for automated version updates:

```bash
#!/bin/bash
# Version update script for AI agents

# Set new version
NEW_VERSION="8.4.3"
NEW_VERSION_CODE="843"

# Update Android
sed -i "s/versionCode = [0-9]*/versionCode = $NEW_VERSION_CODE/" app/android/build.gradle.kts
sed -i "s/versionName = \"[^\"]*\"/versionName = \"$NEW_VERSION\"/" app/android/build.gradle.kts

# Update Desktop
sed -i "s/packageVersion = \"[^\"]*\"/packageVersion = \"$NEW_VERSION\"/" app/desktop/build.gradle.kts

# Update iOS project.pbxproj
sed -i "s/MARKETING_VERSION = [^;]*/MARKETING_VERSION = $NEW_VERSION/" app/iosApp/iosApp.xcodeproj/project.pbxproj

# Update iOS Info.plist
sed -i "s/<string>[0-9]\+\.[0-9]\+\.[0-9]\+<\/string>/<string>$NEW_VERSION<\/string>/" app/iosApp/iosApp/Info.plist

echo "Version updated to $NEW_VERSION"
```

## Verification Steps

After updating versions, verify:

1. **Build successfully**:
   ```bash
   ./gradlew build
   ```

2. **Check version consistency**:
   - Android: `versionCode` and `versionName` are logical
   - Desktop: `packageVersion` follows SemVer
   - iOS: `MARKETING_VERSION` and `CFBundleShortVersionString` match

3. **Test on platforms**:
   ```bash
   # Android
   ./gradlew :app:android:assembleDebug
   
   # Desktop
   ./gradlew :app:desktop:run
   
   # iOS (requires Xcode)
   cd iosApp && pod install
   ```

## Common Mistakes to Avoid

### ❌ Wrong: Inconsistent Version Numbers
```kotlin
// Android
versionCode = 842
versionName = "8.4.1"  // ❌ Doesn't match versionCode

// Desktop
packageVersion = "3.0.0"  // ❌ Different from Android

// iOS
MARKETING_VERSION = 4.0;  // ❌ Different from others
```

### ✅ Correct: Consistent Version Numbers
```kotlin
// Android
versionCode = 842
versionName = "8.4.2"  // ✅ Matches pattern

// Desktop
packageVersion = "8.4.2"  // ✅ Same as Android

// iOS
MARKETING_VERSION = 8.4.2;  // ✅ Same as others
```

### ❌ Wrong: Skipping Files
- Only updating Android but not Desktop/iOS
- Updating `project.pbxproj` but not `Info.plist`
- Forgetting to increment `versionCode`

### ✅ Correct: Complete Update
- Update all platform files
- Ensure all version numbers are consistent
- Increment `versionCode` for Android
- Update both iOS files

## Troubleshooting

### Build Fails After Version Update
1. Check for syntax errors in version strings
2. Ensure all files are properly updated
3. Run `./gradlew clean build`

### CI/CD Workflow Not Triggered
1. Verify tag format matches workflow trigger pattern
2. Ensure tag was pushed to correct branch
3. Check GitHub Actions tab for workflow status

### Version Mismatch in App Stores
1. Verify `versionCode` is higher than previous Android uploads
2. Check iOS version is higher than previous App Store submissions
3. Ensure all platform versions are consistent

## Best Practices

1. **Always update all platforms** when releasing
2. **Use consistent version numbers** across platforms
3. **Test builds** before creating tags
4. **Follow semantic versioning** for user-facing versions
5. **Document changes** in commit messages
6. **Create tags** only after successful local builds

## Quick Reference

### Files to Update
- `app/android/build.gradle.kts` - Android version
- `app/desktop/build.gradle.kts` - Desktop version
- `app/iosApp/iosApp.xcodeproj/project.pbxproj` - iOS project version
- `app/iosApp/iosApp/Info.plist` - iOS bundle version

### Tag Patterns
- `android/*` - Triggers Android workflow
- `desktop/*` - Triggers Desktop workflow
- `ios/*` - Triggers iOS workflow

### Commands
```bash
# Update and commit
git add app/*/build.gradle.kts app/iosApp/iosApp.xcodeproj/project.pbxproj app/iosApp/iosApp/Info.plist
git commit -m "chore: bump version to X.Y.Z"

# Create and push tags
git tag android/X.Y.Z && git push origin android/X.Y.Z
git tag desktop/X.Y.Z && git push origin desktop/X.Y.Z
git tag ios/X.Y.Z && git push origin ios/X.Y.Z
```

This guide ensures AI agents can reliably update versions and trigger CI/CD workflows across all platforms in the NoteDelight project.
