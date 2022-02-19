package com.softartdev.notedelight.shared.base

expect object IdlingResource {
    fun increment()
    fun decrement()
}