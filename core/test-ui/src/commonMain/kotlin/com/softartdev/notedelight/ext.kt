@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelectable
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.waitUntilDoesNotExist
import co.touchlab.kermit.Logger

const val ASSERT_WAIT_TIMEOUT_MILLIS: Long = 20_000
const val MAX_RETRY_ATTEMPTS = 100

inline fun retryUntilDisplayed(
    description: String,
    action: () -> Unit,
    sni: SemanticsNodeInteraction,
): SemanticsNodeInteraction {
    var displayed: Boolean = runCatching(sni::assertIsDisplayed).isSuccess
    var retries = 0
    while (!displayed && retries < MAX_RETRY_ATTEMPTS) {
        action.invoke()
        retries++
        displayed = runCatching(sni::assertIsDisplayed).isSuccess
    }
    Logger.withTag("ℹ️retryUntilDisplayed").i { "After $retries retries is $displayed for $description" }
    return sni.assertIsDisplayed()
}

inline fun ComposeUiTest.waitUntilDisplayed(
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

fun ComposeUiTest.waitUntilNotExist(tag: String) = waitUntilDoesNotExist(
    matcher = hasTestTag(tag),
    timeoutMillis = ASSERT_WAIT_TIMEOUT_MILLIS,
)

inline fun ComposeUiTest.waitAssert(
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

inline fun ComposeUiTest.waitUntilSelected(
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


