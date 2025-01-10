package com.softartdev.notedelight.util

import kotlinx.datetime.LocalDateTime
import platform.Foundation.NSDateFormatter

actual object DateTimeFormatter {
    private val nsDateFormatter = NSDateFormatter()

    init {
        nsDateFormatter.setDateFormat(dateFormat = PATTERN)
    }

    actual fun format(input: LocalDateTime): String = nsDateFormatter.stringFromDate(
        date = input.toNSDate()
    )
}
