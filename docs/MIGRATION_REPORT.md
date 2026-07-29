# Migration Report: CocoaPods to SwiftPM Import

**Project:** NoteDelight
**Modules migrated:** Repository-wide iOS integration, with SQLCipher owned by `:core:data:db-sqldelight` and `:core:data:db-room`
**Date:** 2026-06-04
**Kotlin version:** 2.4.0 -> 2.4.0
**Status:** Completed successfully for the active SQLDelight configuration; optional Room verification is blocked by a pre-existing version-catalog error

> **Current configuration:** The deployment target was raised to iOS 15.0 after this migration so it matches the Firebase 12.14 requirement. References to iOS 14.0/14.1 below describe the historical migration state.

---

## Pre-Migration State

### CocoaPods Dependencies

| Pod | Version | Mode | Modules |
|-----|---------|------|---------|
| SQLCipher | 4.9.0 | Regular cinterop owner | `:core:data:db-sqldelight`, `:core:data:db-room` |
| SQLCipher | 4.9.0 | `linkOnly` | `:app:ios-kit`, `:core:ui`, `:core:test:ui` |
| iosComposePod | Local KMP pod | App dependency | `app/iosApp/Podfile` |

### Framework Configuration

| Module | Framework | Before | After |
|--------|-----------|--------|-------|
| `:app:ios-kit` | `iosComposeKit` | Dynamic | Dynamic |
| `:core:ui` | Default module framework | Dynamic | Dynamic |
| `:core:test:ui` | Default module framework | Dynamic | Dynamic |
| `:core:data:db-sqldelight` | Default module framework | Dynamic | Dynamic |
| `:core:data:db-room` | Default module framework | Static | Static |

The CocoaPods Gradle blocks declared iOS 14.0 while the Podfile and Xcode app used iOS 14.1. The SwiftPM migration standardizes all migrated Gradle and Xcode integration on iOS 14.1.

### Kotlin Files Using `cocoapods.*` Imports

| File | Imports |
|------|---------|
| `core/data/db-sqldelight/src/iosMain/kotlin/com/softartdev/notedelight/db/IosCipherUtils.kt` | SQLCipher SQLite constants and functions from `cocoapods.SQLCipher` |
| `core/data/db-room/src/iosMain/kotlin/com/softartdev/notedelight/db/IosCipherUtils.kt` | `cocoapods.SQLCipher.*` |

### Non-KMP CocoaPods

None. The Podfile contained only the local `iosComposePod` dependency.

### Atypical Project Configuration

- SQLCipher was declared repeatedly as `linkOnly` in transitive consumers instead of only by its direct Kotlin cinterop owners.
- `:core:test:ui` used a CocoaPods-era `disableIosReleaseTasks()` helper.
- The repository intentionally requires dynamic Kotlin and SQLCipher frameworks, despite the higher runtime-linkage risk.
- The active database module is selected through `CORE_DATA_DB_MODULE`; SQLDelight was active before migration and remains active.

---

## Migration Steps

### Phase 2: Gradle Configuration

- Kept Kotlin at 2.4.0.
- Removed the CocoaPods plugin catalog entry and root `apply false` declaration.
- Updated SQLCipher from 4.9.0 to exact version 4.16.0.
- Changed `xcodeproj` from `app/iosApp/iosApp.xcworkspace` to `app/iosApp/iosApp.xcodeproj`.
- Raised the Gradle and Kotlin daemon heaps from 16 GiB to 24 GiB after Kotlin/Native release linkage exhausted the prior heap.
- Removed the dead `disableIosReleaseTasks()` convention helper.
- Removed CocoaPods task exclusions from `gradle/build_quick.sh` and added exclusions for the native framework tasks that replace them.

### Phase 3: `swiftPMDependencies`

Only the direct SQLCipher owners declare the package:

```kotlin
group = "com.softartdev.notedelight"

swiftPMDependencies {
    iosMinimumDeploymentTarget = "14.1"
    discoverClangModulesImplicitly = false
    swiftPackage(
        url = url("https://github.com/sqlcipher/SQLCipher.swift.git"),
        version = exact(libs.versions.iosSqlCipher.get()),
        products = listOf(product("SQLCipher")),
        importedClangModules = listOf("SQLCipher"),
    )
}
```

Their generated `swiftPMImport` cinterops receive `-DSQLITE_HAS_CODEC`, which keeps SQLCipher-only APIs such as `sqlite3_key` visible:

```kotlin
iosTarget.compilations.getByName("main").cinterops.configureEach {
    if (name == "swiftPMImport") {
        compilerOpts("-DSQLITE_HAS_CODEC")
    }
}
```

All migrated modules use native `binaries.framework` configuration and a SwiftPM deployment target of 14.1. Each framework also sets `-Xoverride-konan-properties=minVersion.ios=14.1` because Kotlin/Native 2.4 otherwise emitted a minimum iOS version of 15.0.

The group was added only to the direct SQLCipher owners. Compose resource module groups were deliberately left unchanged to avoid changing generated resource namespaces.

### Phase 4: Import Transformations

| File | Before | After | Source |
|------|--------|-------|--------|
| `core/data/db-sqldelight/src/iosMain/kotlin/com/softartdev/notedelight/db/IosCipherUtils.kt` | `cocoapods.SQLCipher.*` symbols | `swiftPMImport.com.softartdev.notedelight.core.data.db.sqldelight.*` symbols | Generated SwiftPM cinterop |
| `core/data/db-room/src/iosMain/kotlin/com/softartdev/notedelight/db/IosCipherUtils.kt` | `cocoapods.SQLCipher.*` | `swiftPMImport.com.softartdev.notedelight.core.data.db.room.*` | Generated SwiftPM cinterop |

No `cocoapods.*` imports were preserved.

### Phase 5: iOS Project Reconfiguration

The Xcode integrations were generated with:

```bash
XCODEPROJ_PATH=/Users/artur/AndroidStudioProjects/NoteDelight/app/iosApp/iosApp.xcodeproj \
GRADLE_PROJECT_PATH=:app:ios-kit \
./gradlew :app:ios-kit:integrateEmbedAndSign :app:ios-kit:integrateLinkagePackage
```

The app now builds directly from `iosApp.xcodeproj` and references the tracked local `KotlinMultiplatformLinkedPackage`.

Xcode project changes:

- Added Kotlin embed/sign and linkage-package integration.
- Set `ENABLE_USER_SCRIPT_SANDBOXING = NO`.
- Set `SQLITE_HAS_CODEC=1`.
- Added `import SQLCipher` to `CipherChecker.swift`.
- Added the existing `Note Delight Unit-Tests` target to the shared scheme test action.
- Removed CocoaPods phases, configurations, references, bridging headers, and explicit `-lsqlite3`.
- Tracked stable linkage-package sources/manifests and SwiftPM `Package.resolved` files while ignoring checkouts and build caches.

### Phase 6: CocoaPods Removal

- Removed all `kotlin("native.cocoapods")`/catalog plugin declarations and all `cocoapods {}` blocks.
- Removed `linkOnly` SQLCipher declarations from transitive consumers.
- Removed generated podspecs from `app/ios-kit`, `core/data/db-sqldelight`, `core/test/ui`, and `core/ui`.
- Ran `pod deintegrate`.
- Removed `app/iosApp/Podfile`, `Podfile.lock`, the tracked `Pods/` directory, the app-level `.xcworkspace`, and bridging headers.
- Updated active documentation, iOS CI, and Fastlane to use SwiftPM and the `.xcodeproj`.
- Left historical changelog entries unchanged.

### Phase 7: Verification

Active SQLDelight configuration:

- SQLDelight and `ios-kit` iOS simulator compile/link tasks passed.
- `app/iosApp/iosApp.xcodeproj` built successfully.
- Xcode unit test `Note_Delight_Unit_Tests.testCipherChecker()` passed and asserted `4.16.0 community`.
- The app launched and remained running on Simulator without a dynamic-framework or dyld failure.
- `xcrun vtool` confirmed iOS minimum version 14.1 for `iosComposeKit` and `db_sqldelight`.

Optional Room configuration:

- The module was temporarily included and selected.
- Configuration failed before iOS migration compilation because `core/data/db-room/build.gradle.kts` references the undefined accessor `libs.cashapp.paging.common`.
- SQLDelight was restored as the final active database module.

Final mandatory repository sequence:

1. `./gradle/build_quick.sh` - passed in 1m 34s.
2. `./gradlew :app:android:connectedCheck` - passed in 3m 15s; 22 tests, 1 skipped, 0 failed.
3. `./gradlew build` - passed in 8m 32s; 1,368 actionable tasks, including all migrated iOS release frameworks.

---

## Errors Encountered

### Error #1: Relative Xcode Project Path Generated the Linkage Package in the Wrong Location

**Phase:** Xcode integration
**Symptom:** The linkage package was generated outside the intended iOS app directory.
**Root cause:** `XCODEPROJ_PATH` was relative during the initial integration run.
**Fix:** Re-ran the integration with the absolute `.xcodeproj` path and removed the duplicate generated package.
**Generalizable:** Yes.

### Error #2: Kotlin/Native Emitted iOS 15.0 Frameworks

**Phase:** Framework verification
**Symptom:** Xcode warned that `iosComposeKit` required iOS 15.0 despite the project target being 14.1.
**Root cause:** Kotlin/Native 2.4's default minimum iOS version overrode the SwiftPM deployment declaration for produced binaries.
**Fix:** Added `-Xoverride-konan-properties=minVersion.ios=14.1` to every migrated iOS framework binary.
**Generalizable:** Yes.

### Error #3: Shared Scheme Did Not Run the SQLCipher Unit Test

**Phase:** Xcode verification
**Symptom:** The shared scheme had no test target in its TestAction.
**Root cause:** The existing unit-test target was not included in the shared scheme.
**Fix:** Added `Note Delight Unit-Tests` to `iosApp.xcscheme`.
**Generalizable:** No.

### Error #4: Optional Room Module Cannot Configure

**Phase:** Optional Room verification
**Symptom:** Gradle reported an unresolved `libs.cashapp.paging.common` accessor at `core/data/db-room/build.gradle.kts:57`.
**Root cause:** The optional module has a pre-existing version-catalog mismatch unrelated to CocoaPods or SwiftPM.
**Fix:** Documented the failure and restored SQLDelight as the final active module.
**Generalizable:** No.

### Error #5: `pod deintegrate` Reported a Ruby Interpreter Warning

**Phase:** CocoaPods removal
**Symptom:** The command reported a bad Ruby interpreter warning.
**Root cause:** The local CocoaPods launcher referenced an unavailable Ruby interpreter.
**Fix:** `pod deintegrate` still completed successfully; the resulting Xcode project was inspected and verified.
**Generalizable:** Yes.

### Error #6: Quick Build Initially Linked New Native Framework Tasks

**Phase:** Mandatory verification
**Symptom:** `gradle/build_quick.sh` ran migrated `linkDebugFramework*` and `linkReleaseFramework*` tasks.
**Root cause:** Its exclusions only knew the former CocoaPods task names.
**Fix:** Added exclusions for the new native framework link task names and removed obsolete CocoaPods exclusions.
**Generalizable:** Yes.

### Error #7: Kotlin/Native Release Linkage Exhausted the 16 GiB Gradle Heap

**Phase:** Mandatory verification
**Symptom:** Release linkage failed with `GC overhead limit exceeded`.
**Root cause:** The migrated full build links several native dynamic frameworks and exceeded the previous heap.
**Fix:** Raised Gradle and Kotlin daemon heap limits to 24 GiB.
**Generalizable:** Project-dependent.

### Error #8: Stale Mixed KLIB Identities Broke the First Full Build

**Phase:** Mandatory verification
**Symptom:** `:core:ui:linkDebugTestIosSimulatorArm64` reported `Unknown dependent library NoteDelight.core.data:db-sqldelight`.
**Root cause:** Build output contained a mixture of pre-migration and post-migration KLIB identities after adding the direct-owner group.
**Fix:** Ran `./gradlew clean`, then restarted the mandatory sequence.
**Generalizable:** Yes.

### Error #9: Long-Lived Gradle Daemon Disappeared Near the End of Full Build

**Phase:** Mandatory verification
**Symptom:** Gradle reported `Gradle build daemon disappeared unexpectedly` after successful native linkage output.
**Root cause:** The daemon had accumulated several hours of memory-intensive native-link work; no source or task failure appeared in its log.
**Fix:** Recycled Gradle daemons between mandatory checks and reran the complete sequence successfully.
**Generalizable:** Project-dependent.

### Error #10: Android Studio Reported a Missing Linkage-Package Product

**Phase:** Android Studio iOS run verification
**Symptom:** Android Studio failed with `Missing package product 'KotlinMultiplatformLinkedPackage'`, while Xcode and command-line `xcodebuild` succeeded.
**Root cause:** Android Studio's Xcode BuildService used separate DerivedData whose SwiftPM `workspace-state.json` contained no dependencies. Deleting or recreating the iOS run configuration did not resolve packages into that state.
**Fix:** Resolve dependencies with `Tools > Swift Package Manager > Resolve Dependencies` before the Android Studio iOS run. For verification, the command-line equivalent populated Android Studio's DerivedData, after which the app built, installed, and launched successfully.
**Generalizable:** Yes.

---

## Non-Trivial Decisions

- Kept dynamic frameworks for `iosComposeKit`, SQLDelight, core UI, and UI tests because that runtime policy was explicitly required. The optional Room framework remains static.
- Declared SQLCipher only in direct Kotlin cinterop owners. Transitive consumers rely on Kotlin linkage-package integration instead of redeclaring the package.
- Used exact SQLCipher 4.16.0 even though this intentionally upgrades from CocoaPods 4.9.0.
- Disabled implicit Clang-module discovery and explicitly imported only `SQLCipher` to keep the generated cinterop narrow and deterministic.
- Applied `SQLITE_HAS_CODEC` in both generated Kotlin cinterops and the Xcode app so `sqlite3_key` remains visible to Kotlin and Swift.
- Tracked generated linkage-package manifests/sources and lockfiles because Xcode needs stable package integration, while generated checkouts and build products remain ignored.
- Required an Xcode unit test and Simulator launch because dynamic SQLCipher/Kotlin framework embedding failures can pass compile/link checks but fail at runtime.

---

## Files Changed

### Gradle Files

- `.gitignore` - tracks stable SwiftPM integration output while excluding generated checkouts and build caches.
- `build.gradle.kts` - removed root CocoaPods plugin declaration.
- `gradle/libs.versions.toml` - removed CocoaPods plugin alias and pinned SQLCipher 4.16.0.
- `gradle.properties` - selected `.xcodeproj` and increased native-link heap.
- `gradle/build_quick.sh` - replaced CocoaPods task exclusions with native framework exclusions.
- `build-logic/convention/src/main/kotlin/com/softartdev/notedelight/ProjectExtensions.kt` - removed dead iOS release-task helper.
- `app/ios-kit/build.gradle.kts` - native dynamic framework and SwiftPM linkage configuration.
- `core/ui/build.gradle.kts` - native dynamic framework and SwiftPM linkage configuration.
- `core/test/ui/build.gradle.kts` - native dynamic framework and SwiftPM linkage configuration.
- `core/data/db-sqldelight/build.gradle.kts` - direct SQLCipher SwiftPM owner and dynamic framework.
- `core/data/db-room/build.gradle.kts` - direct SQLCipher SwiftPM owner and static framework.

### Kotlin and Swift Sources

- `core/data/db-sqldelight/src/iosMain/kotlin/com/softartdev/notedelight/db/IosCipherUtils.kt` - generated SwiftPM import namespace.
- `core/data/db-room/src/iosMain/kotlin/com/softartdev/notedelight/db/IosCipherUtils.kt` - generated SwiftPM import namespace.
- `core/data/db-sqldelight/src/iosTest/kotlin/com/softartdev/notedelight/CryptTest.kt` - SQLCipher 4.16.0 assertion.
- `app/iosApp/iosApp/CipherChecker.swift` - imports SQLCipher.
- `app/iosApp/Note Delight Unit-Tests/Note_Delight_Unit_Tests.swift` - SQLCipher 4.16.0 assertion.

### Xcode, CI, and Fastlane

- `app/iosApp/iosApp.xcodeproj/project.pbxproj` - Kotlin integrations, local linkage package, codec definition, and CocoaPods cleanup.
- `app/iosApp/iosApp.xcodeproj/xcshareddata/xcschemes/iosApp.xcscheme` - shared unit-test action.
- `.github/workflows/ios.yml` - native framework prebuild.
- `app/iosApp/fastlane/Fastfile` - direct `.xcodeproj` builds.

### Documentation

- `AGENTS.md`, `CONTRIBUTING.md`, `build-logic/README.md`
- `app/ios-kit/README.md`, `app/iosApp/README.md`
- `core/data/db-sqldelight/README.md`, `core/test/ui/README.md`
- `docs/AI_AGENT_GUIDE.md`, `docs/ARCHITECTURE.md`, `docs/MANUAL_BUILD_INSTALL.md`, `docs/OPEN_EXPORTED_DB.md`, `docs/README.md`, `docs/TESTING_GUIDE.md`

### Created

- `MIGRATION_REPORT.md` - this report.
- `.swiftpm-locks/default/.gitignore` - excludes the generated SwiftPM checkout cache.
- `app/iosApp/KotlinMultiplatformLinkedPackage/` - stable generated Xcode linkage-package sources and manifests.
- `app/iosApp/iosApp.xcodeproj/project.xcworkspace/xcshareddata/swiftpm/Package.resolved` - Xcode SwiftPM lockfile.
- `.swiftpm-locks/default/swiftImport/Package.resolved` - Kotlin Swift import lockfile.

### Deleted

- `app/iosApp/Podfile`, `app/iosApp/Podfile.lock`, and tracked `app/iosApp/Pods/`.
- `app/iosApp/iosApp.xcworkspace/`.
- iOS app and unit-test bridging headers.
- Generated podspecs from `app/ios-kit`, `core/data/db-sqldelight`, `core/test/ui`, and `core/ui`.
