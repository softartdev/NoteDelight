package com.softartdev.notedelight.compose

import android.util.Log
import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel

class CustomAntilog : Antilog() {

    private val customTag = BuildConfig.APPLICATION_ID

    override fun performLog(
        priority: LogLevel,
        tag: String?,
        throwable: Throwable?,
        message: String?
    ) {
        log(priority, customTag, message, throwable)
    }

    private fun log(
        priority: LogLevel,
        tag: String?,
        message: String?,
        throwable: Throwable?
    ): Int = when (priority) {
        LogLevel.VERBOSE -> Log.v(tag, message, throwable)
        LogLevel.DEBUG -> Log.d(tag, message, throwable)
        LogLevel.INFO -> Log.i(tag, message, throwable)
        LogLevel.WARNING -> Log.w(tag, message, throwable)
        LogLevel.ERROR -> Log.e(tag, message, throwable)
        LogLevel.ASSERT -> Log.wtf(tag, message, throwable)
    }
}
