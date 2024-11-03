# Note Delight 📝🔐

[![Kotlin Multiplatform CI](https://github.com/softartdev/NoteDelight/actions/workflows/kmp.yml/badge.svg)](https://github.com/softartdev/NoteDelight/actions/workflows/kmp.yml)
[![Android CD](https://github.com/softartdev/NoteDelight/actions/workflows/android.yml/badge.svg)](https://github.com/softartdev/NoteDelight/actions/workflows/android.yml)
[![iOS CD](https://github.com/softartdev/NoteDelight/actions/workflows/ios.yml/badge.svg)](https://github.com/softartdev/NoteDelight/actions/workflows/ios.yml)
[![Desktop Java CD](https://github.com/softartdev/NoteDelight/actions/workflows/desktop.yaml/badge.svg)](https://github.com/softartdev/NoteDelight/actions/workflows/desktop.yaml)

[Kotlin Multiplatform](https://kotlinlang.org/lp/mobile/) application for create notes in SQLite
database with [SQLDelight](https://github.com/cashapp/sqldelight) library, and encrypt it
with [Cipher](https://www.zetetic.net/sqlcipher/). The Compose-UI also has dark/light themes.

Supported platforms:

- Android
- iOS (+ macOS [(Mac Catalyst)](https://developer.apple.com/mac-catalyst/))
- Desktop JVM (macOS, Linux, Windows)x(x86_64, arm64)

[![google_play_badge](screenshoots/badge-google-play.svg)](https://play.google.com/store/apps/details?id=com.softartdev.noteroom)
[![app_store_badge](screenshoots/badge-app-store.svg)](https://apps.apple.com/ge/app/note-delight/id6444444290)
<a href="https://github.com/softartdev/NoteDelight/releases"><img src="screenshoots/badge-github.webp" alt="github_badge" height="40"></a>

## ARCHITECTURE 🏛

![Architecture blueprint for this project](screenshoots/architecture.png)

## SCREENSHOTS 🎞️

<img src="screenshoots/android/dark/1.png" height="447"> <img src="screenshoots/ios/light/5.png" height="447"> <img src="screenshoots/desktop/dark/6.png" height="447">
<details>
    <summary>More…</summary>
    <p><img src="screenshoots/android/dark/1.png" height="447"> <img src="screenshoots/ios/dark/1.png" height="447"> <img src="screenshoots/desktop/dark/1.png" height="447"></p>
    <p><img src="screenshoots/android/light/1.png" height="447"> <img src="screenshoots/ios/light/1.png" height="447"> <img src="screenshoots/desktop/light/1.png" height="447"></p>
    <p><img src="screenshoots/android/dark/2.png" height="447"> <img src="screenshoots/ios/dark/2.png" height="447"> <img src="screenshoots/desktop/dark/2.png" height="447"></p>
    <p><img src="screenshoots/android/light/2.png" height="447"> <img src="screenshoots/ios/light/2.png" height="447"> <img src="screenshoots/desktop/light/2.png" height="447"></p>
    <p><img src="screenshoots/android/dark/3.png" height="447"> <img src="screenshoots/ios/dark/3.png" height="447"> <img src="screenshoots/desktop/dark/3.png" height="447"></p>
    <p><img src="screenshoots/android/light/3.png" height="447"> <img src="screenshoots/ios/light/3.png" height="447"> <img src="screenshoots/desktop/light/3.png" height="447"></p>
    <p><img src="screenshoots/android/dark/4.png" height="447"> <img src="screenshoots/ios/dark/4.png" height="447"> <img src="screenshoots/desktop/dark/4.png" height="447"></p>
    <p><img src="screenshoots/android/light/4.png" height="447"> <img src="screenshoots/ios/light/4.png" height="447"> <img src="screenshoots/desktop/light/4.png" height="447"></p>
    <p><img src="screenshoots/android/dark/5.png" height="447"> <img src="screenshoots/ios/dark/5.png" height="447"> <img src="screenshoots/desktop/dark/5.png" height="447"></p>
    <p><img src="screenshoots/android/light/5.png" height="447"> <img src="screenshoots/ios/light/5.png" height="447"> <img src="screenshoots/desktop/light/5.png" height="447"></p>
    <p><img src="screenshoots/android/dark/6.png" height="447"> <img src="screenshoots/ios/dark/6.png" height="447"> <img src="screenshoots/desktop/dark/6.png" height="447"></p>
    <p><img src="screenshoots/android/light/6.png" height="447"> <img src="screenshoots/ios/light/6.png" height="447"> <img src="screenshoots/desktop/light/6.png" height="447"></p>
    <p><img src="screenshoots/android/dark/7.png" height="447"> <img src="screenshoots/ios/dark/7.png" height="447"> <img src="screenshoots/desktop/dark/7.png" height="447"></p>
    <p><img src="screenshoots/android/light/7.png" height="447"> <img src="screenshoots/ios/light/7.png" height="447"> <img src="screenshoots/desktop/light/7.png" height="447"></p>
    <p><img src="screenshoots/android/dark/8.png" height="447"> <img src="screenshoots/ios/dark/8.png" height="447"> <img src="screenshoots/desktop/dark/8.png" height="447"></p>
    <p><img src="screenshoots/android/light/8.png" height="447"> <img src="screenshoots/ios/light/8.png" height="447"> <img src="screenshoots/desktop/light/8.png" height="447"></p>
</details>

## WORK IN PROGRESS 🛠

| feature \ platform | Android | iOS | Desktop Java |
|:------------------:|:-------:|:---:|:------------:|
|      database      |    ✅    | ✅	  |      ✅	      |
|     encryption     |    ✅    | ✅ 	 |              |
|         ui         |    ✅    | ✅	  |      ✅	      |

Check out [CONTRIBUTING.md](/CONTRIBUTING.md) if you want to develop missing features.

## CONTINUOUS INTEGRATION / DELIVERY ♻️

![CI/CD workflows blueprint for this project](screenshoots/ci_cd.png)

## LIBRARY DEPENDENCIES 📚

- [SQLDelight](https://github.com/cashapp/sqldelight)
- [SQLCipher](https://github.com/sqlcipher/sqlcipher)
- [Compose Multiplatform, by JetBrains](https://github.com/JetBrains/compose-jb)
- [MaterialThemePrefs](https://github.com/softartdev/MaterialThemePrefs)
- [kotlinx-coroutines](https://github.com/Kotlin/kotlinx.coroutines)
- [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime)
- [Koin](https://github.com/InsertKoinIO/koin)
- [CWAC-SafeRoom](https://github.com/commonsguy/cwac-saferoom)
- [Napier](https://github.com/AAkira/Napier)
- [Firebase Crashlytics](https://firebase.google.com/products/crashlytics)
- [LeakCanary 🐤](https://github.com/square/leakcanary)
- [Mockito](https://github.com/mockito/mockito)
- [Turbine](https://github.com/cashapp/turbine)
- [Orchestrator](https://developer.android.com/training/testing/instrumented-tests/androidx-test-libraries/runner#use-android)
- [Espresso](https://developer.android.com/training/testing/espresso)

[![Jetbrains Logo](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg)](https://jb.gg/OpenSourceSupport)

Made with JetBrains tools
