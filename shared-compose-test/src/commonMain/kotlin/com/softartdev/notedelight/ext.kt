@file:OptIn(ExperimentalTestApi::class, ExperimentalTestApi::class)

package com.softartdev.notedelight

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import io.github.aakira.napier.Napier

const val ASSERT_WAIT_TIMEOUT_MILLIS: Long = 60_000

inline fun ComposeUiTest.waitUntilDisplayed(
    crossinline blockSNI: () -> SemanticsNodeInteraction,
) = waitUntil(timeoutMillis = ASSERT_WAIT_TIMEOUT_MILLIS) {
    try {
        val sni = blockSNI()
        sni.assertIsDisplayed()
    } catch (e: AssertionError) {
        Napier.e("Node is not displayed while waiting", e)
        return@waitUntil false
    }
    return@waitUntil true
}

inline fun ComposeUiTest.waitAssert(
    crossinline assert: () -> Unit
) = waitUntil(timeoutMillis = ASSERT_WAIT_TIMEOUT_MILLIS) {
    try {
        assert()
    } catch (e: AssertionError) {
        Napier.e("Assertion failed while waiting", e)
        return@waitUntil false
    }
    return@waitUntil true
}
