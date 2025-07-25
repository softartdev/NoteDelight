@file:OptIn(ExperimentalTime::class)

package com.softartdev.notedelight.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.ExperimentalTime

const val PATTERN = "HH:mm dd-MM-yyyy"

val LocalDateTime.posixMillis: Long
    get() = toInstant(timeZone = TimeZone.currentSystemDefault()).toEpochMilliseconds()

expect object DateTimeFormatter {

    fun format(input: LocalDateTime): String
}