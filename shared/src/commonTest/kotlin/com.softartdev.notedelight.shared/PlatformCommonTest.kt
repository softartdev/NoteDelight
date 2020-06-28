package com.softartdev.notedelight.shared

import kotlin.test.Test
import kotlin.test.assertEquals

class PlatformCommonTest {

    @Test
    fun createCommonMessageTest() {
        assertEquals("Kotlin Multiplatform on ${platformName()}", createMultiplatformMessage())
    }
}