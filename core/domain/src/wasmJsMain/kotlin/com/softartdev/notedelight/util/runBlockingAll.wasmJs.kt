@file:OptIn(ExperimentalWasmJsInterop::class, DelicateCoroutinesApi::class)
@file:Suppress("UNCHECKED_CAST")

package com.softartdev.notedelight.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine

actual fun <T> runBlockingAll(context: CoroutineContext, block: suspend CoroutineScope.() -> T): T {
    var out: Result<T>? = null
    val continuation: Continuation<T> = Continuation(context) { out = it }
    block.startCoroutine(receiver = GlobalScope, completion = continuation)
    return out!!.getOrThrow()
}