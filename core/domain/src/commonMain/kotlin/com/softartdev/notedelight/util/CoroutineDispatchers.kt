package com.softartdev.notedelight.util

import kotlinx.coroutines.CoroutineDispatcher

interface CoroutineDispatchers {
    val default: CoroutineDispatcher
    val main: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
    val io: CoroutineDispatcher
}

expect class CoroutineDispatchersImpl() : CoroutineDispatchers {
    override val default: CoroutineDispatcher
    override val main: CoroutineDispatcher
    override val unconfined: CoroutineDispatcher
    override val io: CoroutineDispatcher
}
