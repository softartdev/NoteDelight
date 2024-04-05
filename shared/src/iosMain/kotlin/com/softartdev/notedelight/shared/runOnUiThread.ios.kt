@file:OptIn(ExperimentalForeignApi::class, ExperimentalForeignApi::class)

package com.softartdev.notedelight.shared

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.memScoped
import platform.Foundation.NSDate
import platform.Foundation.NSRunLoop
import platform.Foundation.distantFuture
import platform.Foundation.runUntilDate
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual fun <T> runOnUiThread(block: () -> T): T {
    memScoped {
        val resultStableRef = StableRef.create(block)
        val errorStableRef = StableRef.create(ThrowableWrapper(null))

        dispatch_async(dispatch_get_main_queue()) {
            try {
                resultStableRef.get()()
            } catch (e: Throwable) {
                errorStableRef.get().throwable = e
            }
        }
        NSRunLoop.currentRunLoop().runUntilDate(NSDate.distantFuture())

        errorStableRef.get().throwable?.let { throw it }
        return resultStableRef.get()()
    }
}

class ThrowableWrapper(var throwable: Throwable?)
