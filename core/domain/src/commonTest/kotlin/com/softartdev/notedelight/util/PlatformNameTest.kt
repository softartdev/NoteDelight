package com.softartdev.notedelight.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PlatformNameTest {

    @Test
    fun createMultiplatformMessageTest() {
        assertEquals("Kotlin Multiplatform on ${platformName()}", createMultiplatformMessage())
    }

    @Test
    fun appVersionReturnsNonEmpty() {
        val version = appVersion()
        assertTrue(version.isNotBlank(), "appVersion() should return non-blank string, got: '$version'")
    }
}