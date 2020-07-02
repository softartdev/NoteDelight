package com.softartdev.notedelight.shared

import kotlin.test.Test
import kotlin.test.assertEquals

class PlatformIosTest {

    @Test
    fun platformIosNameTest() {
        assertEquals("iOS", platformName())
    }

    @Test
    fun createIosMessageTest() {
        assertEquals("Kotlin Multiplatform on iOS", createMultiplatformMessage())
    }
}