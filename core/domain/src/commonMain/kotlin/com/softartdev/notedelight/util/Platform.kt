package com.softartdev.notedelight.util

enum class Platform(val emoji: String) { Android("🤖"), IOS("🍎"), Desktop("🖥️"), Web("🌐") }

expect val platform: Platform

fun createMultiplatformMessage() : String = "Kotlin Multiplatform on ${platform.name}"
