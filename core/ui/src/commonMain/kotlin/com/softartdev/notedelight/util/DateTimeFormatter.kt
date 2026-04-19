@file:OptIn(ExperimentalTime::class)

package com.softartdev.notedelight.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.char
import kotlin.time.ExperimentalTime

object DateTimeFormatter {
    private val localDateTimeFormat: DateTimeFormat<LocalDateTime> = LocalDateTime.Format {
        hour(); char(':'); minute(); char(' '); day(); char('-'); monthNumber(); char('-'); year()
    }
    fun format(input: LocalDateTime): String = input.format(localDateTimeFormat)
}