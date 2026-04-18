package com.softartdev.notedelight.util

import co.touchlab.kermit.Logger
import com.softartdev.notedelight.PrintLogWriter
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PlatformTest {
    private val logger = Logger.withTag("PlatformTest")

    @BeforeTest
    fun setUp() = Logger.setLogWriters(PrintLogWriter())

    @AfterTest
    fun tearDown() = Logger.setLogWriters()

    @Test
    fun createMultiplatformMessageTest() {
        logger.i { "Testing on ${platform.emoji}" }
        assertEquals("Kotlin Multiplatform on ${platform.name}", createMultiplatformMessage())
    }
}