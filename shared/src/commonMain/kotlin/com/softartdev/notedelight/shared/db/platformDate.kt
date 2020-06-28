package com.softartdev.notedelight.shared.db

import com.squareup.sqldelight.ColumnAdapter

expect class Date(timeInMillis: Long)

expect class DateAdapter() : ColumnAdapter<Date, Long>