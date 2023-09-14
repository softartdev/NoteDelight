package com.softartdev.notedelight.shared.base

import io.github.aakira.napier.Napier
import java.util.concurrent.atomic.AtomicInteger

actual object IdlingRes {
    private val counter = AtomicInteger(0)

    actual val isIdleNow: Boolean
        get() = counter.get() == 0

    actual fun increment() {
        val counterVal = counter.getAndIncrement()
        Napier.d("Idling resource in-use-count incremented to: ${counterVal + 1}")
    }

    actual fun decrement()  {
        val counterVal = counter.decrementAndGet()
        if (counterVal == 0) {
            Napier.d("Idling resource went idle!")
        } else {
            Napier.d("Idling resource in-use-count decremented to: $counterVal")
        }
        check(counterVal > -1) { "Counter has been corrupted! counterVal=$counterVal" }
    }
}