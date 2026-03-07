package com.softartdev.notedelight.util

expect fun platformName(): String

expect fun appVersion(): String

fun createMultiplatformMessage(): String = "Kotlin Multiplatform on ${platformName()}"
