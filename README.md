# NoteDelight 📝🔐
![Kotlin Multiplatform CI](https://github.com/softartdev/NoteDelight/workflows/Kotlin%20Multiplatform%20CI/badge.svg)
![Fastlane CI/CD Android](https://github.com/softartdev/NoteDelight/workflows/Fastlane%20CI/CD%20Android/badge.svg)
![iOS starter workflow](https://github.com/softartdev/NoteDelight/workflows/iOS%20starter%20workflow/badge.svg)
[![Desktop Java CI/CD](https://github.com/softartdev/NoteDelight/actions/workflows/desktop.yaml/badge.svg)](https://github.com/softartdev/NoteDelight/actions/workflows/desktop.yaml)

[Kotlin Multiplatform](https://kotlinlang.org/lp/mobile/) application for create notes in SQLite database with [SQLDelight](https://github.com/cashapp/sqldelight) library, and encrypt it with [Cipher](https://www.zetetic.net/sqlcipher/).

The Android version also has dark/light themes.🤖

An iOS version is under development.📱

<img src="https://github.com/softartdev/NoteDelight/raw/master/demo_android.gif" height="500" />    <img src="https://github.com/softartdev/NoteDelight/raw/master/demo_ios.gif" height="500" />

<p>
  <a href="https://play.google.com/store/apps/details?id=com.softartdev.noteroom"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge-border.png" height="75px"/></a>
</p>

## JMV-Desktop powered by JetBrans-Compose 🖥
[comment]: <TODO> (![]&#40;screenshoots/snap-store-black.svg&#41;)
[comment]: <TODO> (![]&#40;screenshoots/Download_on_the_Mac_App_Store_Badge_US-UK_RGB_blk_092917.svg&#41;)
[comment]: <TODO> (![]&#40;screenshoots/English_get it from MS_864X312.svg&#41;)

![](screenshoots/linux/anigif.gif)
![](screenshoots/mac/anigif.gif)
![](screenshoots/win/anigif.gif)

## WORK IN PROGRESS 🛠
We need an iOS developer. If you have the skill to create apps with SwiftUI and are enthusiastic to contribute to open source, you can pull-request and/or [contact me](https://t.me/Archi_bald) for a collaboration experience.

We are also working on shared code for the multi-platform module.

## ARCHITECTURE 🏛
![Architecture blueprint for this project](architecture.png)

## HISTORY 📜

This project is a fork of [NoteRoom](https://github.com/softartdev/NoteRoom) (which in turn is a fork of [NoteCrypt](https://github.com/softartdev/NoteCrypt)).

Migrated to [SQLDelight](https://github.com/cashapp/sqldelight) from [Room](https://developer.android.com/topic/libraries/architecture/room) Persistence Library.
