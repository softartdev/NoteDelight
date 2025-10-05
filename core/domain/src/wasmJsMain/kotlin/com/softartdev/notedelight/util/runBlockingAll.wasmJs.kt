@file:OptIn(ExperimentalWasmJsInterop::class, DelicateCoroutinesApi::class)
@file:Suppress("UNCHECKED_CAST")

package com.softartdev.notedelight.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.coroutines.CoroutineContext

actual fun <T> runBlockingAll(context: CoroutineContext, block: suspend CoroutineScope.() -> T): T {
    return GlobalScope.promise(context) { block() } as T
}