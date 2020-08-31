package com.softartdev.notedelight.shared.date

import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.util.*

actual fun getSystemTimeInMillis(): Long = System.currentTimeMillis()

fun LocalDateTime.toJvmDate(): Date {
    val instant = this.toInstant(TimeZone.currentSystemDefault())
    val posixMillis = instant.toEpochMilliseconds()
    return Date(posixMillis)
}

fun Date.toLocalDateTime(): LocalDateTime = Instant.fromEpochMilliseconds(
    epochMilliseconds = time
).toLocalDateTime(TimeZone.currentSystemDefault())