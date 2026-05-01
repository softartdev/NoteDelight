package com.softartdev.notedelight

import android.os.Looper
import androidx.test.platform.app.InstrumentationRegistry
import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask

/**
 * Runs the given action on the UI thread.
 *
 * This method is blocking until the action is complete.
 *
 * @throws Throwable Any exception that is thrown on the UI thread during execution of [action]. The
 *   thrown exception contains a suppressed [ExecutionException] that contains the stacktrace on the
 *   calling side.
 */
actual fun <T> runOnUiThread(action: () -> T): T {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        return action()
    }

    // Note: This implementation is directly taken from ActivityTestRule
    val task: FutureTask<T> = FutureTask(action)
    InstrumentationRegistry.getInstrumentation().runOnMainSync(task)
    try {
        return task.get()
    } catch (e: ExecutionException) {
        // Throw the original exception, but add a new ExecutionException as a suppressed error
        // to expose the caller's thread's stacktrace. We have to create a new ExecutionException
        // to be able to remove the cause, for otherwise we would create a circular reference
        // (cause --suppresses--> e --causedBy--> cause --suppresses--> e --etc-->)
        throw e.cause?.also {
            it.addSuppressed(
                ExecutionException(
                    "An Exception occurred on the UI thread during runOnUiThread()",
                    null,
                )
            )
        } ?: e
    }
}
