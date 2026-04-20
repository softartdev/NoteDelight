package com.softartdev.notedelight.util

import androidx.compose.ui.test.IdlingResource

object ComposeCountingIdlingResource : IdlingResource {

    override val isIdleNow: Boolean
        get() = CountingIdlingRes.isIdleNow

    override fun getDiagnosticMessageIfBusy(): String =
        "Idling resource with counter = ${CountingIdlingRes.counter}"
}