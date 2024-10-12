package com.softartdev.notedelight.shared

import com.softartdev.notedelight.shared.util.CoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher

class CoroutineDispatchersStub(testDispatcher: TestDispatcher) : CoroutineDispatchers {

    constructor(scheduler: TestCoroutineScheduler) : this(StandardTestDispatcher(scheduler))

    override val default: CoroutineDispatcher = testDispatcher
    override val main: CoroutineDispatcher = testDispatcher
    override val unconfined: CoroutineDispatcher = testDispatcher
    override val io: CoroutineDispatcher = testDispatcher
}
