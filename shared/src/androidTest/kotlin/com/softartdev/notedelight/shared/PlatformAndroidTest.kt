package com.softartdev.notedelight.shared

import org.junit.Test

import org.junit.Assert.*

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