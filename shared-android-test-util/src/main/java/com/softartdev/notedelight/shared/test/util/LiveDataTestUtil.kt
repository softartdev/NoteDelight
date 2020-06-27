/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.softartdev.notedelight.shared.test.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import org.junit.Assert.fail
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Gets the value of a [LiveData] or waits for it to have one, with a timeout.
 *
 * Use this extension from host-side (JVM) tests. It's recommended to use it alongside
 * `InstantTaskExecutorRule` or a similar mechanism to execute tasks synchronously.
 */
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = Observer<T> { o ->
        data = o
        latch.countDown()
//            this@getOrAwaitValue.removeObserver(this)
    }
    this.observeForever(observer)
    afterObserve.invoke()
    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        throw TimeoutException("LiveData value was never set.")
    }
    this.removeObserver(observer)
    @Suppress("UNCHECKED_CAST")
    return data as T
}

/**
 * @see <a href="https://www.reddit.com/r/androiddev/comments/9tux6h/how_to_unit_test_livedata_values_in_our_viewmodels/e900pmy?utm_source=share&utm_medium=web2x>Reddit answer</a>
 */
fun <T> LiveData<T>.assertValues(
        vararg expectedRaw: T,
        timeout: Long = 2,
        block: () -> Unit = {}
) {
    val actualValues = mutableListOf<T>()
    val latch = CountDownLatch(expectedRaw.size)

    val observer = spy(Observer<T> {
        actualValues += it
        latch.countDown()
    })
    observeForever(observer)
    block.invoke()
    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(timeout, TimeUnit.SECONDS)) {
        fail("LiveData value was never set.")
    }
    removeObserver(observer)
    // Arrays do not print prettily, so convert them to a list
    val expectedValues: List<T> = expectedRaw.asList()
    if (actualValues.size > expectedValues.size) {
        fail("LiveData emitted more values than expected\nExpected: $expectedValues\nActual  : $actualValues")
    }
    if (actualValues.size < expectedValues.size) {
        fail("LiveData emitted fewer values than expected\nExpected: $expectedValues\nActual  : $actualValues")
    }
    expectedValues.zip(actualValues).forEachIndexed { i, (expect, actual) ->
        if (expect != actual) fail("Values emitted at index $i do not match\nExpected: $expectedValues\nActual  : $actualValues")
    }
    verify(observer, times(expectedValues.size)).onChanged(any())
}

