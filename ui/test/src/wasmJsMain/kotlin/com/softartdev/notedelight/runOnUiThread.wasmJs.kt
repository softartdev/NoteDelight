package com.softartdev.notedelight

/**
 * Runs the given action on the UI thread.
 *
 * This method is blocking until the action is complete.
 */
actual fun <T> runOnUiThread(action: () -> T): T {
    return action()
}
