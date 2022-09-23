package com.softartdev.notedelight.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.softartdev.notedelight.shared.base.IdlingResource.countingIdlingResource

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
