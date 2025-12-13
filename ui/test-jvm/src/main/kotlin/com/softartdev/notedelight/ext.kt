package com.softartdev.notedelight

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelectable
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import co.touchlab.kermit.Logger

const val ASSERT_WAIT_TIMEOUT_MILLIS: Long = 5_000

inline fun retryUntilDisplayed(
    description: String,
    action: () -> Unit,
    sni: SemanticsNodeInteraction,
): SemanticsNodeInteraction {
    var displayed: Boolean = runCatching(sni::isDisplayed).isSuccess
    var retries = 0
    while (!displayed) {
        action.invoke()
        retries++
        displayed = runCatching(sni::isDisplayed).isSuccess
    }
    Logger.withTag("ℹ️retryUntilDisplayed").i { "After $retries retries displayed: $description" }
    return sni.assertIsDisplayed()
}

inline fun ComposeTestRule.waitUntilDisplayed(
    description: String,
    crossinline blockSNI: () -> SemanticsNodeInteraction,
) = waitUntil(conditionDescription = description, timeoutMillis = ASSERT_WAIT_TIMEOUT_MILLIS) {
    try {
        val sni = blockSNI()
        sni.assertIsDisplayed()
    } catch (_: AssertionError) {
        return@waitUntil false
    }
    return@waitUntil true
}

inline fun ComposeTestRule.waitAssert(
    description: String,
    crossinline assert: () -> Unit
) = waitUntil(conditionDescription = description, timeoutMillis = ASSERT_WAIT_TIMEOUT_MILLIS) {
    try {
        assert()
    } catch (_: AssertionError) {
        return@waitUntil false
    }
    return@waitUntil true
}

inline fun ComposeTestRule.waitUntilSelected(
    description: String,
    crossinline blockSNI: () -> SemanticsNodeInteraction
) = waitUntil(conditionDescription = description, timeoutMillis = ASSERT_WAIT_TIMEOUT_MILLIS) {
    val sni = blockSNI().assertIsSelectable()
    try {
        sni.assertIsSelected()
    } catch (_: AssertionError) {
        return@waitUntil false
    }
    return@waitUntil true
}
