package com.softartdev.notedelight.shared.date

import kotlinx.datetime.*
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import kotlin.system.getTimeMillis
import kotlin.time.ExperimentalTime

actual fun getSystemTimeInMillis(): Long = getTimeMillis()

@OptIn(ExperimentalTime::class)
fun LocalDateTime.toNSDate(): NSDate {
    val referenceDateTime = LocalDateTime(2001, 1, 1, 0, 0, 0, 0)
    val referenceInstant: Instant = referenceDateTime.toInstant(TimeZone.currentSystemDefault())
    val instant: Instant = toInstant(TimeZone.currentSystemDefault())
    val duration = instant - referenceInstant
    val secondsFromReferenceDate = duration.inSeconds
    return NSDate(secondsFromReferenceDate)
}

fun NSDate.toLocalDateTime(): LocalDateTime = Instant.fromEpochSeconds(
    epochSeconds = timeIntervalSince1970.toLong()
).toLocalDateTime(TimeZone.currentSystemDefault())