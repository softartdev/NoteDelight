package com.softartdev.notedelight

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity

class PrintLogWriter : LogWriter() {

    private val Severity.char: Char
        get() = when (this) {
            Severity.Verbose -> 'V'
            Severity.Debug -> 'D'
            Severity.Info -> 'I'
            Severity.Warn -> 'W'
            Severity.Error -> 'E'
            Severity.Assert -> 'A'
        }

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        println("${severity.char}/$tag: $message${throwable.traceStack}")
    }

    private val Throwable?.traceStack: String
        get() = this?.let { throwable -> "\n\t${throwable.stackTraceToString()}" } ?: ""
}

