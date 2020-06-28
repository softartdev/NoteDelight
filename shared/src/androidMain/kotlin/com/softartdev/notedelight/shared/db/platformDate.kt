package com.softartdev.notedelight.shared.db

import com.squareup.sqldelight.ColumnAdapter

actual typealias Date = java.util.Date

actual class DateAdapter actual constructor() : ColumnAdapter<Date, Long> {
    override fun encode(value: Date): Long = value.time
    override fun decode(databaseValue: Long): Date = java.util.Date(databaseValue)
}
