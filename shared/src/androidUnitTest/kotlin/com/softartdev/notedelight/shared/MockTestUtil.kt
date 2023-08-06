package com.softartdev.notedelight.shared

import org.mockito.Mockito

//https://stackoverflow.com/a/30308199/8436645
fun <T> anyObject(): T {
    Mockito.any<T>()
    return uninitialized()
}

@Suppress("UNCHECKED_CAST")
private fun <T> uninitialized(): T = null as T