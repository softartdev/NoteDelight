package com.softartdev.notedelight.shared.db

import com.squareup.sqldelight.ColumnAdapter
import platform.Foundation.NSDate

actual class Date actual constructor(timeInMillis: Long) : NSDate(
    timeIntervalSinceReferenceDate = timeInMillis.toDouble() / 1000
)

actual class DateAdapter actual constructor() : ColumnAdapter<Date, Long> {
    override fun decode(databaseValue: Long): Date = Date(databaseValue)
    override fun encode(value: Date): Long = value.timeIntervalSinceReferenceDate.toLong() * 1000L
}
