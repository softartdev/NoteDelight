package com.softartdev.notedelight.util

import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class DateTimeFormatterTest {

    @Test
    fun formatTest() {
        val dateTime = LocalDateTime(2024, 6, 15, 14, 30)
        val formatted: String = DateTimeFormatter.format(dateTime)
        assertEquals(expected = "14:30 15-06-2024", actual = formatted)
    }
}