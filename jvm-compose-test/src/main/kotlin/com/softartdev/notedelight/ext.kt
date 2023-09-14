package com.softartdev.notedelight

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import io.github.aakira.napier.Napier

inline fun ComposeTestRule.waitUntilDisplayed(
    crossinline blockSNI: () -> SemanticsNodeInteraction,
) = waitUntil(timeoutMillis = 10_000) {
    try {
        val sni = blockSNI()
        sni.assertIsDisplayed()
    } catch (e: AssertionError) {
        Napier.e("Node is not displayed while waiting", e)
        return@waitUntil false
    }
    return@waitUntil true
}

inline fun ComposeTestRule.waitAssert(
    crossinline assert: () -> Unit
) = waitUntil(timeoutMillis = 10_000) {
    try {
        assert()
    } catch (e: AssertionError) {
        Napier.e("Assertion failed while waiting", e)
        return@waitUntil false
    }
    return@waitUntil true
}
