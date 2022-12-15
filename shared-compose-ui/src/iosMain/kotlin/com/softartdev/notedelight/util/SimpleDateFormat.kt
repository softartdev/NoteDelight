package com.softartdev.notedelight.util

import com.softartdev.notedelight.shared.date.toNSDate
import kotlinx.datetime.LocalDateTime
import platform.Foundation.NSDateFormatter

actual object SimpleDateFormat {

    private val nsDateFormatter = NSDateFormatter()

    init {
        nsDateFormatter.setDateFormat(dateFormat = "HH:mm dd-MM-yyyy")
    }

    actual fun format(input: LocalDateTime): String = nsDateFormatter.stringFromDate(
        date = input.toNSDate()
    )
}