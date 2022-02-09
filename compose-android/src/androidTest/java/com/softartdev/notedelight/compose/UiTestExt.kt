package com.softartdev.notedelight.compose

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule


fun SemanticsNodeInteractionsProvider.togglePasswordVisibility(
    testTag: String
): SemanticsNodeInteraction = onNodeWithTag(testTag, useUnmergedTree = true).performClick()

inline fun ComposeTestRule.advancePerform(block: () -> Unit) {
    mainClock.autoAdvance = false
    block()
    Thread.sleep(10)
    mainClock.autoAdvance = true
}
