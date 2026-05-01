package com.softartdev.notedelight

import kotlinx.coroutines.runBlocking
import platform.Foundation.NSDate
import platform.Foundation.NSDefaultRunLoopMode
import platform.Foundation.NSRunLoop
import platform.Foundation.performBlock
import platform.Foundation.runMode
import kotlin.coroutines.suspendCoroutine

/**
 * Runs the given action on the UI thread.
 *
 * This method is blocking until the action is complete.
 */
actual fun <T> runOnUiThread(action: () -> T): T {
    return if (NSRunLoop.currentRunLoop === NSRunLoop.mainRunLoop) {
        action()
    } else {
        runBlocking {
            suspendCoroutine {
                NSRunLoop.mainRunLoop.performBlock {
                    it.resumeWith(kotlin.runCatching { action() })
                }
                NSRunLoop.mainRunLoop.runMode(NSDefaultRunLoopMode, NSDate.new()!!)
            }
        }
    }
}
