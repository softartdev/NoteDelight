package com.softartdev.notedelight.util

expect fun platformName(): String

fun createMultiplatformMessage() : String = "Kotlin Multiplatform on ${platformName()}"
