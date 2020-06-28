package com.softartdev.notedelight.shared

expect fun platformName(): String

fun createMultiplatformMessage() : String {
    return "Kotlin Multiplatform on ${platformName()}"
}