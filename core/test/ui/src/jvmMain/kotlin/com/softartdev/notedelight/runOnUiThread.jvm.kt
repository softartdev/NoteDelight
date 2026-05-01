package com.softartdev.notedelight

import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask
import javax.swing.SwingUtilities

/**
 * Runs the given action on the UI thread.
 *
 * This method is blocking until the action is complete.
 */
actual fun <T> runOnUiThread(action: () -> T): T {
    return if (SwingUtilities.isEventDispatchThread()) {
        action()
    } else {
        val task: FutureTask<T> = FutureTask(action)
        SwingUtilities.invokeAndWait(task)
        try {
            return task.get()
        } catch (e: ExecutionException) { // Expose the original exception
            throw e.cause!!
        }
    }
}
