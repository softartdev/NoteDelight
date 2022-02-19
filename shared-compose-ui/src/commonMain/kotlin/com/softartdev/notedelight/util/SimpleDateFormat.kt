package com.softartdev.notedelight.util

import kotlinx.datetime.LocalDateTime

//TODO commonize jvm & android actual implementations
expect object SimpleDateFormat {

    fun format(input: LocalDateTime): String
}