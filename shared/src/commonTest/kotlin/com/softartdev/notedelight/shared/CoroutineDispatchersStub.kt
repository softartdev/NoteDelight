package com.softartdev.notedelight.shared

import com.softartdev.notedelight.shared.util.CoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.test.TestDispatcher

@ExperimentalCoroutinesApi
class CoroutineDispatchersStub(testDispatcher: TestDispatcher) : CoroutineDispatchers {
    override val default: CoroutineDispatcher = testDispatcher
    override val main: CoroutineDispatcher = testDispatcher
    override val unconfined: CoroutineDispatcher = testDispatcher
    override val io: CoroutineDispatcher = Dispatchers.IO
}
