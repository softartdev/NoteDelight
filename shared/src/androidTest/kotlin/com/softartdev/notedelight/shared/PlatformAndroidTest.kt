package com.softartdev.notedelight.shared

import org.junit.Assert.assertEquals
import org.junit.Test

class PlatformAndroidTest {

    @Test
    fun platformAndroidNameTest() {
        assertEquals("Android", platformName())
    }

    @Test
    fun createAndroidMessageTest() {
        assertEquals("Kotlin Multiplatform on Android", createMultiplatformMessage())
    }
}