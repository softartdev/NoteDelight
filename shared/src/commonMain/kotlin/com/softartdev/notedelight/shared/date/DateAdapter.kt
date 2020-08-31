package com.softartdev.notedelight.shared.date

import com.squareup.sqldelight.ColumnAdapter
import kotlinx.datetime.*

class DateAdapter : ColumnAdapter<LocalDateTime, Long> {

    override fun encode(value: LocalDateTime): Long = value
        .toInstant(TimeZone.currentSystemDefault())
        .toEpochMilliseconds()

    override fun decode(databaseValue: Long): LocalDateTime = Instant
        .fromEpochMilliseconds(epochMilliseconds = databaseValue)
        .toLocalDateTime(TimeZone.currentSystemDefault())
}