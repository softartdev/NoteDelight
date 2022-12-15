package com.softartdev.notedelight.shared.date

import kotlinx.datetime.*
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import kotlin.time.Duration
import kotlin.time.DurationUnit

fun LocalDateTime.toNSDate(): NSDate {
    val referenceDateTime = LocalDateTime(2001, 1, 1, 0, 0, 0, 0)
    val referenceInstant: Instant = referenceDateTime.toInstant(TimeZone.UTC)
    val instant: Instant = toInstant(TimeZone.currentSystemDefault())
    val duration: Duration = instant - referenceInstant
    val secondsFromReferenceDate: Double = duration.toDouble(DurationUnit.SECONDS)
    return NSDate(secondsFromReferenceDate)
}

fun NSDate.toLocalDateTime(): LocalDateTime = Instant
    .fromEpochSeconds(epochSeconds = timeIntervalSince1970.toLong(), nanosecondAdjustment = 0)
    .toLocalDateTime(timeZone = TimeZone.currentSystemDefault())
