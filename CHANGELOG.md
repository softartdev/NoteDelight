# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [8.4.9] - 2025-12-23

### Features
- Upgrade Kotlin and dependencies
- Add test tag for Enter Password dialog
- Set launchSingleTop for navigation: Add navigation tests for Android and Desktop platforms
- Add test tag to Change Password dialog and new test helpers

### Bug Fixes
- Stabilize UI tests with Idling Resources

### Refactoring
- Improve UI test stability and maintainability
- Create multiplatform `ui:test` module and move UI tests
- Simplify navigation methods and improve null handling in RouterImpl
- Improve UI test retry logic and stability

### Tests
- Add screenshot generation test for desktop app

### Documentation
- Add Navigation Testing section to TESTING_GUIDE

### Chores
- Remove build dependency for Android UI test job

## [8.4.8] - 2025-12-13
### Features
- Enable SQLCipher encryption for Desktop JVM

## [8.4.7] - 2025-12-06
### Features
- Add settings navigation from SignIn screen and BackHandler

## [8.4.6] - 2025-11-23
### Features
- Implement in-app language switching

## [8.4.5] - 2025-11-15
### Features
- Add in-app file explorer

## [8.4.4] - 2025-11-07
### Features
- Update dependencies and improve Android UI

## [8.4.3] - 2025-11-04
### Refactoring
- Decouple Snackbar logic and implement OPFS for Web

## [8.4.2] - 2025-10-28
### Features
- Implement adaptive UI for tablets and large screens

## [8.4.1] - 2025-10-09
### Other
- Remove dependency on ui_test_job in build unit test publish job
- Update sqlcipher for Android and migrate to fork of cwac-saferoom; project structure and documentation; increment version numbers; improve CI configurations

## [8.4.0] - 2025-10-05
### Other
- Add wasmJs target; refactor project structure; rename files and directories for consistency; update coroutine usage in use cases

## [8.3.9] - 2025-07-24
### Other
- Update password dialog titles and subtitles; improve localization strings; set app_name as non-translatable; enable locale config generation

## [8.3.8] - 2025-07-23
## [8.3.7] - 2025-03-23
### Bug Fixes
- fix JvmTestSafeRepo dbPath
- fix unit-tests

### Refactoring
- refactoring result states

### Other
- update github actions checkout & cache
- Scrollbar as LinearProgressIndicator
- connecting room as another one core-data-db module
- pull to refresh
- Paging & DB file path snackbar (#519)
- type-safe navigation

## [8.3.6] - 2024-10-13
### Other
- up app versions
- move navigation above view layer
- migrate from moko-res to jb

## [8.3.5] - 2024-07-05
### Other
- updates: kotlin 2.0.0, etc
- workaround for upload crashlytics task
- convention gradle plugin
- rotation android ui test
- update agp
- compose 1.6.0-beta02
- mokoResources 0.24.0-alpha-3
- mokoResources 0.24.0-alpha-3
- kotlin 1.9.22 compose 1.6.0-beta01
- mokoResources 0.24.0-alpha-2, materialThemePrefs 0.6.2
- Update README.md

## [8.3.4] - 2023-11-22
### Other
- up Android & iOS versions for publications
- trim note title
- ErrorDialog shows 1 button
- show progress indicator on splash screen
- update kotlin to 1.9.20 & compose to 1.5.10

## [8.3.3] - 2023-11-11
### Bug Fixes
- fix for Xcode 15 on CI
- fix for Xcode 15

### Other
- update architecture diagram
- create noteDAO
- materialThemePrefs to 0.5.9
- remove old darwin flags
- increase timeout for ui-tests
- commonize ui-tests for android & desktop targets

## [8.3.2] - 2023-09-04
### Bug Fixes
- fix_edit_title_crash

### Other
- removing redundant modules
- add ui-test for editing title after note create
- add missing screenshots
- migrate to material3
- update compose, fix decompose
- remove old files 🪦
- add full class package name for run JVM (desktop-compose-app) (#417)

## [8.3.1] - 2023-02-02
### Bug Fixes
- fixing ui-test
- fixing ui-test
- fixing ui-test
- fix iOS fastlane
- fix-up ios
- fix-up ios

### Other
- update agp
- sign-out ui-test
- handle navigate to sign-in on main screen error
- Gradle Wrapper Validation Action
- Gradle Wrapper Validation Action
- scroll up on list updated
- Update README.md
- update materialThemePrefs
- update koin
- Update README.md
- update diagrams
- increment build number
- up ios build version
- run_precheck_before_submit: false
- increment_version_number

## [8.3] - 2022-12-15
### Bug Fixes
- fix-up tests
- fix test data

### Other
- up android version to 8.3
- remove keeper
- update README.md
- remove unused
- android:windowSoftInputMode="adjustResize"
- 🍾 iOS app is published on AppStore 🚀
- run ios-compose-app on ipad
- compose image painter for ios
- add for ios-old-app: id, name
- add for ios-compose-app: id, name, icon
- add iosSimulatorArm64 target for compose-app
- update decompose
- update agp
- update jb-compose to 1.2.0
- update decompose

## [8.2] - 2022-09-02
### Bug Fixes
- fix CDATA for moko-resources
- fix create note id
- fix ios
- fix preview
- fix ios

### Other
- update jb-compose to v1.2.0-alpha01-dev774
- add remaining ios targets
- update materialThemePrefs to 0.3.8
- ios target
- add -lsqlite3 to linkerOpts
- temporarily make function non-composable
- update materialThemePrefs to 0.3.7
- update decompose to 1.0.0-alpha-04-native-compose
- update jb-compose to v1.2.0-alpha01-dev770
- update jb-compose to v1.2.0-alpha01-dev764 & Kotlin 1.7.10
- update materialThemePrefs to "0.3.6"
- update compose-jb to 1.2.0-alpha01-dev755
- creating module ios-compose-app
- rename module desktop to desktop-compose-app
- prepare jb-compose for iOS

## [8.1] - 2022-07-18
### Bug Fixes
- fix fastlane

## [8.0] - 2022-06-13
### Bug Fixes
- fix ui tests
- fix prepopulate database test
- fix shared module version

### Other
- DiffUtil on old app
- install cocoapods-generate on CI for iOS
- update moko-resources & add pod support files
- wrap transaction for lastInsertRowId
- prepopulate database test
- cocoapods don't use libs

## [7.9] - 2022-05-10
### Other
- disable cocoapods deps & rollback kotlin & compose
- generate def file with headers
- install cocoapods-generate on CI
- source-sets by wizard
- import cocoapods.SQLCipher.*
- update kotlin to 1.6.21 & re-enable pod deps & disable unsupported compose modules
- update moko-resources
- crashlytics antilog unit-test

## [7.8] - 2022-04-17
### Other
- update kotlinx-datetime
- gradle versions catalog
- gradle versions catalog
- kotlinize gradle scripts
- upfix decompose
- downgrade agp for compatibility with idea
- kotlinize module scripts
- kotlinize sittings & build gradle
- create buildSrc dir
- declare shared deps by source sets

## [7.7] - 2022-03-20
## [7.6] - 2022-02-20
### Bug Fixes
- fix moko-resources for ios
- fix package-name tests

### Tests
- testBuildType "release"

### Other
- update firebase_version
- update lifecycle-viewmodel-ktx
- update koin
- update readme
- add keeper & enable core lib desugaring
- enable Crashlytics mapping file upload for specific build types
- commonize custom loggers
- setup firebase analytics & crashlytics
- change android-compose-app package
- get context for moko-resources from koin
- leak canary: skip test detections
- leak canary: obtain by reflection, keep standard dependency for work-manager
- leak canary: commonize process checker; add to compose module; replace test run listener with rule
- change old package
- update material-theme-prefs library

## [7.5] - 2021-09-20
### Bug Fixes
- fix gradle update

### Other
- add api-level for ui-tests on android 12
- workaround min sdk version by override splash screen drawable
- Keep the splash screen on-screen for longer periods
- workaround min sdk version by override splash screen lib
- Migrate your existing splash screen implementation to Android 12
- update compile & target sdk to 31
- archive ui-tests output artifacts
- update pod specs

## [7.4] - 2021-06-27
### Other
- don't decrypt secret keys on CI-workflows
- generate stub signing keys & don't gitignore them
- remove secret google-services.json.gpg
- actualize google-services.json
- don't gitignore google-services.json

## [7.3] - 2021-06-19
### Other
- Remove LifecycleOwner.addRepeatingJob API
- remove deprecated useIR option

## [7.2] - 2021-05-22
### Bug Fixes
- fix build db repo before check on splash screen
- fix swiftui preview 🎉
- fix xcode tests

### Other
- remove cipher-delight module
- remove new cryptdb module
- move utils to shared module
- switch to new module
- copy utils from old module
- add SQLCipher pod
- set path to ios project podfile
- create module from new wizard
- remove jcenter from settings gradle
- unlink sqlite from sqldelight
- add SQLITE_HAS_CODEC compiler flag
- cinterop like sqliter
- update pod lib
- update pod lib after automate publish process
- ✅ cipher from ktn-lib instead cocoapods 🎉

---

## NoteRoom (Android-only) Era

*Note: The project was originally named NoteRoom and was Android-only. It was forked in 2020 to create NoteDelight with multiplatform support. The versions below are from the original [NoteRoom repository](https://github.com/softartdev/NoteRoom).*

## [5.6] - 2020-08-19

### Changed
- Updated README.md
- Workaround Koin issue
- Updated dependencies:
  - Kotlin from 1.3.72 to 1.4.0
  - Coroutines from 1.3.8 to 1.3.9-native-mt
  - Mockito from 3.4.6 to 3.5.2

## [5.5] - 2020-08-14

### Changed
- Fixed UI Espresso tests after Material library update
- Updated dependencies:
  - Firebase Crashlytics from 17.1.1 to 17.2.1
  - Firebase Analytics from 17.4.4 to 17.5.0
  - AndroidX AppCompat from 1.1.0 to 1.2.0
  - Material Components from 1.1.0 to 1.2.0
  - Mockito from 3.4.4 to 3.4.6
- Updated Gradle plugin
- Use preference-ktx library

## [5.4] - 2020-07-27

### Changed
- Updated dependencies:
  - CWAC-SafeRoom from 1.2.1 to 1.3.0

## [5.3] - 2020-07-24

### Changed
- Updated dependencies:
  - Gradle plugin from 4.0.0 to 4.0.1
  - Coroutines from 1.3.7 to 1.3.8
  - Koin from 2.1.5 to 2.1.6
  - Firebase Crashlytics from 17.1.0 to 17.1.1
  - Mockito from 3.3.3 to 3.4.4
- Updated Gradle build tools

## [5.2] - 2020-06-27

### Added
- Modularization: Created shared module and test util module
- Target SDK version updated to 30
- GitHub Actions improvements:
  - Separate job for UI tests
  - Use android-emulator-runner
  - Continue-on-error & fail-fast configuration
  - Workarounds for API-level testing on CI
- Lint fixes and code improvements

### Changed
- Updated libraries
- Optimized Gradle build speed
- Fixed FlowAfterCryptTest
- Test infrastructure improvements

## [4.4] - 2020-05-02

### Changed
- Firebase Crashlytics SDK out of beta
- Updated analytics library
- Updated Koin dependency
- Updated Gradle

## [4.3] - 2020-04-19

### Added
- Flow integration for note list
- FlowAfterCryptTest
- CreateRemoveNoteWithUseCaseTest
- Firebase Crashlytics collection enabled

### Changed
- Upgraded to Firebase Crashlytics SDK
- Relaunch flow by invoke
- Use secondary colors in swipe refresh layout
- Updated Gradle
- Fixed Fastlane skip_upload_apk
- Splash theme background
- Migrated to Material Components
- Using component constructor on base activity
- Archive output artifacts on CI
- Fixed lint issues

### Fixed
- Update notes after init main view model

## [4.2] - 2020-04-19

### Changed
- Firebase Crashlytics collection enabled
- Upgraded to Firebase Crashlytics SDK

## [4.1] - 2020-04-19

### Changed
- Updated libraries
- Firebase Crashlytics collection enabled

## [4.0] - 2020-04-03

### Added
- Comprehensive unit tests:
  - Test for SplashActivity
  - Test for ChangeViewModel
  - Test for ConfirmViewModel
  - Test for EnterViewModel
  - Test for SettingsViewModel
  - Test for EditTitleViewModel
  - Test for NoteUseCase
  - Test for CryptUseCase
- UI test extensions
- Unit tests for view models

### Changed
- Replaced suppress annotation with opt-in
- Updated Gradle

## [3.8] - 2020-04-03

### Changed
- Updated version to 4.0
- Comprehensive testing infrastructure

## [3.7] - 2020-03-04

### Changed
- Updated libraries
- Enabled multidex

## [3.6] - 2020-02-06

### Changed
- ProGuard rules for Crashlytics
- Use Android Test Orchestrator

## [3.5] - 2020-02-03

### Changed
- Code shrinking enabled
- ProGuard rules optimization
- Disabled multidex

## [3.4] - 2020-01-22

### Changed
- Room incremental annotation processor option
- Updated libraries

## [3.3] - 2020-01-22

### Added
- Title bar functionality
- Edit title on notes
- Note loading state
- Dialog fragment for editing
- Toast notification on note deleted

### Changed
- Reformatted results on view models
- Fixed edit title on new note
- Updated libraries

## [3.1] - 2020-01-05

### Changed
- Lint fixes

## [3.0] - 2020-01-05

### Changed
- Lint fixes
- GitHub Actions improvements
- Upgrade CWAC-SafeRoom & Migrating to the New Format

## [2.9] - 2020-01-04

### Changed
- Upgrade CWAC-SafeRoom & Migrating to the New Format
- Upgraded dependencies
- Use flowable for note list

## [2.8] - 2019-12-20

### Fixed
- Fixed tests
- Fixed remove dialog icon for old APIs

## [2.7] - 2019-11-24

### Changed
- Replaced presenters with view models (MVVM migration)
- Upgraded Dagger version

## [2.6] - 2019-11-23

### Fixed
- Fixed remove dialog icon for old APIs

## [2.5] - 2019-10-14

### Changed
- Updated versions
- Hide task snapshot

## [2.4] - 2019-09-24

### Added
- Preferences category for security

### Changed
- Created settings fragment

## [2.3] - 2019-09-07

### Added
- Material Design migration
- Palette support
- Force dark allowed
- Customized EditText widget

### Changed
- Disabled spark gradient
- Updated LeakCanary
- Rx-Room extension & multiDex enabled
- Updated dependencies

## [2.2] - 2019-01-20

### Changed
- Fastlane upload AAB support

## [2.1] - 2019-01-20

### Added
- Fastlane integration
- Firebase Crashlytics integration

### Changed
- Fixed tests
- Migrated to AndroidX
- Enabled Jetifier

## [2.0] - 2018-06-16

### Changed
- Unknown title by default
- Updated Gradle

## [1.6] - 2018-05-05

### Changed
- Updated Gradle
- Renamed package & app name
- Migrated from Realm to Room database
- Added encryption support with SafeRoom
- Added dates created & modified

## [1.5] - 2018-04-05

### Added
- Gradient animations (like Instagram & Spotify)
- Gradient animation on main and security screens
- SecurityActivity converted to Kotlin

### Changed
- Fixed migration when changing password
- Fixed migration

## [1.2] - 2018-03-18

### Added
- Dates created & modified for notes

### Changed
- Fixed migration
- Removed language settings

## [1.1] - 2017-09-02

### Changed
- Updated Kotlin
- Removed storage viewing from release
- Fixed check changes

### Fixed
- Fixed closing instances of Realm

## [1.0] - 2017-05-27

### Added
- Initial release
- Note creation and management
- SignInActivity for security
- Security settings implementation
- Password protection
- Splash screen with security checks
- Theme support
- Delete note action
- Title edit text
- AddDeleteNoteTest

### Changed
- Deleted unnecessary permissions
- Added RxJava support
- Added Kotlin support
- Migrated codebase to Kotlin
- Applied Kotlin KAPT plugin
- Added base architecture components
- Using TextInputEditText instead of EditText
- Annotated application context
- Added Realm browser for debugging

