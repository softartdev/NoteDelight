package com.softartdev.notedelight.util

import com.softartdev.notedelight.shared.date.toJvmDate
import kotlinx.datetime.LocalDateTime
import java.text.SimpleDateFormat
import java.util.*

actual object SimpleDateFormat : SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault()) {

    actual fun format(input: LocalDateTime): String = format(input.toJvmDate())
}