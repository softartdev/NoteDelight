package com.softartdev.notedelight.util.log

import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel

class CrashlyticsAntilog : Antilog() {

    private val crashlytics = FirebaseCrashlytics.getInstance()

    private val LogLevel.logPrefix: String
        get() = when (this) {
            LogLevel.VERBOSE -> "V"
            LogLevel.DEBUG -> "D"
            LogLevel.INFO -> "I"
            LogLevel.WARNING -> "W"
            LogLevel.ERROR -> "E"
            LogLevel.ASSERT -> "A"
        }

    override fun performLog(priority: LogLevel, tag: String?, throwable: Throwable?, message: String?) {
        if (tag != null) {
            crashlytics.log("${priority.logPrefix}/$tag: $message")
        }
        throwable?.let(crashlytics::recordException)
    }
}