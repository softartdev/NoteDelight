@file:OptIn(ExperimentalTime::class)

package com.softartdev.notedelight.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun createLocalDateTime(): LocalDateTime = Clock.System.now()
    .toLocalDateTime(TimeZone.currentSystemDefault())