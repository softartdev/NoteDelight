package com.softartdev.notedelight.shared.db

import com.squareup.sqldelight.ColumnAdapter

expect class Date(timeInMillis: Long) {
    constructor()
}

expect class DateAdapter() : ColumnAdapter<Date, Long>

expect fun getSystemTimeInMillis(): Long