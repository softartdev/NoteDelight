package com.softartdev.notedelight.shared.util

import kotlinx.datetime.LocalDateTime
import java.text.SimpleDateFormat
import java.util.*

object CustomDateFormat : SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault()) {

    fun format(input: LocalDateTime): String = format(input.toJvmDate())
}