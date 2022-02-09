package com.softartdev.notedelight.compose

import androidx.compose.ui.test.*


fun SemanticsNodeInteractionsProvider.togglePasswordVisibility(
    testTag: String
): SemanticsNodeInteraction = onNodeWithTag(testTag, useUnmergedTree = true).performClick()
