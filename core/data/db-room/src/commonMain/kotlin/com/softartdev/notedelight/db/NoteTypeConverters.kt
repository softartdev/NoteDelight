@file:OptIn(ExperimentalTime::class)

package com.softartdev.notedelight.db

import androidx.room3.ColumnTypeConverter
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.jvm.JvmStatic
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object NoteTypeConverters {

    @ColumnTypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?): LocalDateTime? = value
        ?.let(Instant::fromEpochMilliseconds)
        ?.toLocalDateTime(TimeZone.currentSystemDefault())

    @ColumnTypeConverter
    @JvmStatic
    fun dateToTimestamp(date: LocalDateTime?): Long? = date
        ?.toInstant(TimeZone.currentSystemDefault())
        ?.toEpochMilliseconds()
}
