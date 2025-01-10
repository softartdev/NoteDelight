package com.softartdev.notedelight.util

import kotlin.test.Test
import kotlin.test.assertEquals

class PlatformNameTest {

    @Test
    fun createMultiplatformMessageTest() {
        assertEquals("Kotlin Multiplatform on ${platformName()}", createMultiplatformMessage())
    }
}