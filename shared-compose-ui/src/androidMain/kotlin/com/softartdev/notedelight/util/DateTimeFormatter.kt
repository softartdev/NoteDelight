package com.softartdev.notedelight.util

import kotlinx.datetime.LocalDateTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual object DateTimeFormatter : SimpleDateFormat(PATTERN, Locale.getDefault()) {

    actual fun format(input: LocalDateTime): String = format(Date(input.posixMillis))
}
