package com.softartdev.notedelight.shared.base

expect object IdlingRes {
    val isIdleNow: Boolean
    fun increment()
    fun decrement()
}