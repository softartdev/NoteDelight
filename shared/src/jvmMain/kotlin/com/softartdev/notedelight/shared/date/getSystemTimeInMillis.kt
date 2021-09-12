package com.softartdev.notedelight.shared.date

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import java.util.*

actual fun getSystemTimeInMillis(): Long = System.currentTimeMillis()

//TODO: deduplicate with android target
fun LocalDateTime.toJvmDate(): Date {
    val instant = this.toInstant(TimeZone.currentSystemDefault())
    val posixMillis = instant.toEpochMilliseconds()
    return Date(posixMillis)
}