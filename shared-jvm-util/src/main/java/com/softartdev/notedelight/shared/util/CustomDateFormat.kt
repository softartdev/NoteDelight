package com.softartdev.notedelight.shared.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import java.text.SimpleDateFormat
import java.util.*

object CustomDateFormat : SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault()) {

    fun format(input: LocalDateTime): String {
        val instant = input.toInstant(TimeZone.currentSystemDefault())
        val posixMillis = instant.toEpochMilliseconds()
        return format(Date(posixMillis))
    }
}