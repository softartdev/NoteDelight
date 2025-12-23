package com.softartdev.notedelight

/**
 * Runs the given action on the UI thread.
 *
 * This method is blocking until the action is complete.
 */
expect fun <T> runOnUiThread(action: () -> T): T
