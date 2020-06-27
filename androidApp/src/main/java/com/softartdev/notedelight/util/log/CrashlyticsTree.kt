package com.softartdev.notedelight.util.log

import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {

    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (tag != null) {
            crashlytics.log("${priorityToString(priority)}/$tag: $message")
        }
        if (t != null) {
            crashlytics.recordException(t)
        }
    }

    private fun priorityToString(priority: Int): String = when (priority) {
        2 -> "V"
        3 -> "D"
        4 -> "I"
        5 -> "W"
        6 -> "E"
        7 -> "A"
        else -> "?"
    }

}
