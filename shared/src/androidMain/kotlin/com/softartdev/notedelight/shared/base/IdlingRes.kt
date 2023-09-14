package com.softartdev.notedelight.shared.base

import androidx.test.espresso.idling.CountingIdlingResource

actual object IdlingRes {
    val countingIdlingResource = CountingIdlingResource("GLOBAL")

    actual val isIdleNow: Boolean
        get() = countingIdlingResource.isIdleNow

    actual fun increment() {
        countingIdlingResource.increment()
    }

    actual fun decrement()  {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}