package com.softartdev.notedelight.util

import kotlinx.datetime.LocalDateTime

actual object SimpleDateFormat {
    actual fun format(input: LocalDateTime): String = input.toString()//FIXME
}