package com.softartdev.notedelight.shared.util

import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.util.*

fun LocalDateTime.toJvmDate(): Date {
    val instant = this.toInstant(TimeZone.currentSystemDefault())
    val posixMillis = instant.toEpochMilliseconds()
    return Date(posixMillis)
}

fun Date.toLocalDateTime(): LocalDateTime = Instant.fromEpochMilliseconds(
    epochMilliseconds = time
).toLocalDateTime(TimeZone.currentSystemDefault())