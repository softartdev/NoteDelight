package com.softartdev.notedelight.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual class CoroutineDispatchersImpl : CoroutineDispatchers {
    actual override val default: CoroutineDispatcher = Dispatchers.Default
    actual override val main: CoroutineDispatcher = Dispatchers.Main
    actual override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
    actual override val io: CoroutineDispatcher = Dispatchers.Default
}