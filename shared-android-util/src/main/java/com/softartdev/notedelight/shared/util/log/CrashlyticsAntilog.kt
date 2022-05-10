package com.softartdev.notedelight.shared.util.log

import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel

class CrashlyticsAntilog(
    private val crashlytics: FirebaseCrashlytics = FirebaseCrashlytics.getInstance()
) : Antilog() {

    private val logPrefixes: CharArray = LogLevel.values().let { levels: Array<LogLevel> ->
        return@let CharArray(size = levels.size, init = { ordinal: Int -> levels[ordinal].name[0] })
    }

    override fun performLog(
        priority: LogLevel,
        tag: String?,
        throwable: Throwable?,
        message: String?
    ) {
        crashlytics.log("${logPrefixes[priority.ordinal]}/${tag ?: '?'}: $message")
        throwable?.let(crashlytics::recordException)
    }
}