# NoteDelight 📝🔐

![Kotlin Multiplatform CI](https://github.com/softartdev/NoteDelight/workflows/Kotlin%20Multiplatform%20CI/badge.svg)
![Fastlane CI/CD Android](https://github.com/softartdev/NoteDelight/workflows/Fastlane%20CI/CD%20Android/badge.svg)
![iOS starter workflow](https://github.com/softartdev/NoteDelight/workflows/iOS%20starter%20workflow/badge.svg)
[![Desktop Java CI/CD](https://github.com/softartdev/NoteDelight/actions/workflows/desktop.yaml/badge.svg)](https://github.com/softartdev/NoteDelight/actions/workflows/desktop.yaml)

[Kotlin Multiplatform](https://kotlinlang.org/lp/mobile/) application for create notes in SQLite
database with [SQLDelight](https://github.com/cashapp/sqldelight) library, and encrypt it
with [Cipher](https://www.zetetic.net/sqlcipher/). The Compose-UI also has dark/light themes.

Supported platforms:
- Android
- iOS (the iPad version also works on macOS)
- Desktop JVM (macOS, Linux, Windows)

[![google_play_badge](screenshoots/badge-google-play.svg)](https://play.google.com/store/apps/details?id=com.softartdev.noteroom)
[![app_store_badge](screenshoots/badge-app-store.svg)](https://apps.apple.com/ge/app/note-delight/id6444444290)

## ARCHITECTURE 🏛

![Architecture blueprint for this project](screenshoots/architecture.png)

## SCREENSHOTS 🎞️

<img src="https://github.com/softartdev/NoteDelight/raw/master/screenshoots/demo_android.gif" height="447" />    <img src="https://github.com/softartdev/NoteDelight/raw/master/screenshoots/demo_ios.gif" height="447" />    <img src="https://raw.githubusercontent.com/softartdev/NoteDelight/master/screenshoots/demo_desktop.gif" height="447" />

## LIBRARY DEPENDENCIES 📇

- [SQLDelight](https://github.com/cashapp/sqldelight)
- [SQLCipher](https://github.com/sqlcipher/sqlcipher)
- [kotlinx-coroutines](https://github.com/Kotlin/kotlinx.coroutines)
- [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime)
- [Decompose](https://github.com/arkivanov/Decompose)
- [Compose Multiplatform, by JetBrains](https://github.com/JetBrains/compose-jb)
- [MaterialThemePrefs](https://github.com/softartdev/MaterialThemePrefs)
- [moko-resources](https://github.com/icerockdev/moko-resources)
- [Koin](https://github.com/InsertKoinIO/koin)
- [CWAC-SafeRoom](https://github.com/commonsguy/cwac-saferoom)
- [Napier](https://github.com/AAkira/Napier)
- [Firebase Crashlytics](https://firebase.google.com/products/crashlytics)
- [LeakCanary 🐤](https://github.com/square/leakcanary)
- [Mockito](https://github.com/mockito/mockito)
- [Turbine](https://github.com/cashapp/turbine)
- [Orchestrator](https://developer.android.com/training/testing/instrumented-tests/androidx-test-libraries/runner#use-android)
- [Espresso](https://developer.android.com/training/testing/espresso)

## WORK IN PROGRESS 🛠

We need an iOS developer. If you have the skill to create apps with SwiftUI and are enthusiastic to
contribute to open source, you can pull-request and/or [contact me](https://t.me/Archi_bald) for a
collaboration experience.

We are also working on shared code for the multi-platform module.

## HISTORY 📜

This project is a fork of [NoteRoom](https://github.com/softartdev/NoteRoom) (which in turn is a
fork of [NoteCrypt](https://github.com/softartdev/NoteCrypt)).

Migrated to [SQLDelight](https://github.com/cashapp/sqldelight)
from [Room](https://developer.android.com/topic/libraries/architecture/room) Persistence Library.

#

[![Jetbrains Logo](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg)](https://jb.gg/OpenSourceSupport)

Made with JetBrains tools 
