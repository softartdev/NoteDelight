package com.softartdev.notedelight.util

import kotlinx.datetime.LocalDateTime

expect object SimpleDateFormat {

    fun format(input: LocalDateTime): String
}