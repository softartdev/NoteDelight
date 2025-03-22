package com.softartdev.notedelight

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel

class PrintAntilog(private val defaultTag: String = "🦄") : Antilog() {

    private val LogLevel.char: Char
        get() = when (this) {
            LogLevel.DEBUG -> 'D'
            LogLevel.VERBOSE -> 'V'
            LogLevel.INFO -> 'I'
            LogLevel.WARNING -> 'W'
            LogLevel.ERROR -> 'E'
            LogLevel.ASSERT -> 'A'
        }

    private val Throwable?.traceStack: String
        get() = this?.let { throwable -> "\n\t${throwable.stackTraceToString()}" } ?: ""

    override fun performLog(
        priority: LogLevel,
        tag: String?,
        throwable: Throwable?,
        message: String?
    ) = println(message = "${priority.char}/${tag ?: defaultTag}: $message${throwable.traceStack}")
}
