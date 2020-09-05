package com.softartdev.notedelight.shared

import com.softartdev.notedelight.shared.database.DatabaseRepo
import com.softartdev.notedelight.shared.database.IosDbTestRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import platform.CoreFoundation.CFRunLoopGetCurrent
import platform.CoreFoundation.CFRunLoopRun
import platform.CoreFoundation.CFRunLoopStop

actual abstract class BaseTest actual constructor() {

    actual val dbRepo: DatabaseRepo = IosDbTestRepo()

    actual fun <T> runTest(block: suspend CoroutineScope.() -> T) {
        var error: Throwable? = null
        GlobalScope.launch(Dispatchers.Main) {
            try {
                block()
            } catch (t: Throwable) {
                error = t
            } finally {
                CFRunLoopStop(CFRunLoopGetCurrent())
            }
        }
        CFRunLoopRun()
        error?.also { throw it }
    }
}