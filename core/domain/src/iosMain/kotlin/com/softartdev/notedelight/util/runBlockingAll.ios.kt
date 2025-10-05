package com.softartdev.notedelight.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

actual fun <T> runBlockingAll(context: CoroutineContext, block: suspend CoroutineScope.() -> T): T {
    return runBlocking(context, block)
}