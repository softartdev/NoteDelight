package com.softartdev.notedelight.ui

import android.widget.Toast
import androidx.compose.ui.test.ComposeTimeoutException
import androidx.compose.ui.test.IdlingResource
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.softartdev.notedelight.shared.base.IdlingResource.countingIdlingResource
import io.github.aakira.napier.Napier

val composeIdlingResource = object : IdlingResource {
    override val isIdleNow: Boolean
        get() = countingIdlingResource.isIdleNow
}

fun SemanticsNodeInteractionsProvider.togglePasswordVisibility(
    testTag: String
): SemanticsNodeInteraction = onNodeWithTag(testTag, useUnmergedTree = true).performClick()

inline fun ComposeTestRule.advancePerform(block: () -> Unit) {
    mainClock.autoAdvance = false
    block()
    Thread.sleep(40)
    mainClock.autoAdvance = true
}

inline fun AndroidComposeTestRule<*, *>.safeWaitUntil(
    timeoutMillis: Long = 120_000,
    crossinline block: () -> Any
) = try {
    waitUntil(timeoutMillis) {
        try {
            block()
        } catch (e: AssertionError) {
            Napier.e("Node is not displayed", e)
            return@waitUntil false
        }
        return@waitUntil true
    }
} catch (e: ComposeTimeoutException) {
    Napier.e("$timeoutMillis ms condition timed out", e)
    runOnUiThread { Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show() }
}
