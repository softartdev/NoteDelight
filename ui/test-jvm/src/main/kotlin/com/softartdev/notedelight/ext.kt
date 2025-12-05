package com.softartdev.notedelight

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelectable
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import co.touchlab.kermit.Logger

const val UI_TEST_EXT_LOG_TAG: String = "UI_TEST_EXT"
const val ASSERT_WAIT_TIMEOUT_MILLIS: Long = 5_000

inline fun ComposeTestRule.retryUntilDisplayed(
    crossinline action: () -> Unit,
    sni: SemanticsNodeInteraction,
): SemanticsNodeInteraction {
    waitUntil(timeoutMillis = ASSERT_WAIT_TIMEOUT_MILLIS) {
        if (sni.isNotDisplayed()) {
            action()
        }
        return@waitUntil sni.isDisplayed()
    }
    return sni.assertIsDisplayed()
}

inline fun ComposeTestRule.waitUntilDisplayed(
    crossinline blockSNI: () -> SemanticsNodeInteraction,
) = waitUntil(timeoutMillis = ASSERT_WAIT_TIMEOUT_MILLIS) {
    try {
        val sni = blockSNI()
        sni.assertIsDisplayed()
    } catch (e: AssertionError) {
        Logger.e(UI_TEST_EXT_LOG_TAG, e) { "Node is not displayed while waiting" }
        return@waitUntil false
    }
    return@waitUntil true
}

inline fun ComposeTestRule.waitAssert(
    crossinline assert: () -> Unit
) = waitUntil(timeoutMillis = ASSERT_WAIT_TIMEOUT_MILLIS) {
    try {
        assert()
    } catch (e: AssertionError) {
        Logger.e(UI_TEST_EXT_LOG_TAG, e) { "Assertion failed while waiting" }
        return@waitUntil false
    }
    return@waitUntil true
}

inline fun ComposeTestRule.waitUntilSelected(
    crossinline blockSNI: () -> SemanticsNodeInteraction
) = waitUntil(timeoutMillis = ASSERT_WAIT_TIMEOUT_MILLIS) {
    val sni = blockSNI().assertIsSelectable()
    try {
        sni.assertIsSelected()
    } catch (e: AssertionError) {
        Logger.e(UI_TEST_EXT_LOG_TAG, e) { "Node is not selected while waiting" }
        return@waitUntil false
    }
    return@waitUntil true
}
