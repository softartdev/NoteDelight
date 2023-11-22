package com.softartdev.notedelight.shared.base

import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic

actual object IdlingRes {
    private val counter: AtomicInt = atomic(0)

    actual val isIdleNow: Boolean
        get() = counter.value == 0

    actual fun increment() {
        counter.incrementAndGet()
    }

    actual fun decrement() {
        counter.decrementAndGet()
    }
}