package com.softartdev.notedelight.ui.screen

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.MR

@JvmInline
value class MainTestScreen(val composeTestRule: ComposeContentTestRule) {

    val settingsMenuButtonSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithContentDescription(label = MR.strings.settings.contextLocalized())
            .assertIsDisplayed()

    val emptyResultLabelSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithText(text = MR.strings.label_empty_result.contextLocalized())

    val noteListItemSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithContentDescription(label = noteItemTitleText)

    val labelEmptyResultSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithText(MR.strings.label_empty_result.contextLocalized())

    val fabSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithContentDescription(label = MR.strings.create_note.contextLocalized())
            .assertIsDisplayed()

    companion object {
        var noteItemTitleText: String = "Lorem"
    }
}