package com.softartdev.notedelight.db

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.jvm.JvmStatic

object NoteTypeConverters {

    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?): LocalDateTime? = value
        ?.let(Instant::fromEpochMilliseconds)
        ?.toLocalDateTime(TimeZone.currentSystemDefault())

    @TypeConverter
    @JvmStatic
    fun dateToTimestamp(date: LocalDateTime?): Long? = date
        ?.toInstant(TimeZone.currentSystemDefault())
        ?.toEpochMilliseconds()
}
