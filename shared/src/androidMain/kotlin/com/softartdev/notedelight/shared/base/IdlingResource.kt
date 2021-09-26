package com.softartdev.notedelight.shared.base

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource

/**
 * Contains a static reference to [IdlingResource]
 */
actual object IdlingResource {

    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    actual fun increment() {
        countingIdlingResource.increment()
    }

    actual fun decrement()  {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}