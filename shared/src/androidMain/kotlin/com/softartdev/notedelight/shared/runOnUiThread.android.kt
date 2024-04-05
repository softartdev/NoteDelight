package com.softartdev.notedelight.shared

import android.os.Handler
import android.os.Looper

actual fun <T> runOnUiThread(block: () -> T): T {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        return block()
    }
    var result: T? = null
    var error: Throwable? = null
    val handler = Handler(Looper.getMainLooper())
    val runnable = Runnable {
        try {
            result = block()
        } catch (e: Throwable) {
            error = e
        }
    }
    handler.post(runnable)

    error?.let { throw it }
    return result as T
}
