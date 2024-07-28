package com.softartdev.notedelight.ui.screen

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import kotlinx.coroutines.runBlocking
import notedelight.shared_compose_ui.generated.resources.Res
import notedelight.shared_compose_ui.generated.resources.create_note
import notedelight.shared_compose_ui.generated.resources.label_empty_result
import notedelight.shared_compose_ui.generated.resources.settings
import org.jetbrains.compose.resources.getString

@JvmInline
value class MainTestScreen(val composeTestRule: ComposeContentTestRule) {

    val settingsMenuButtonSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithContentDescription(label = runBlocking { getString(Res.string.settings) })
            .assertIsDisplayed()

    val emptyResultLabelSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithText(text = runBlocking { getString(Res.string.label_empty_result) })

    val noteListItemSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithContentDescription(label = noteItemTitleText)

    val labelEmptyResultSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithText(text = runBlocking { getString(Res.string.label_empty_result) })

    val fabSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithContentDescription(label = runBlocking { getString(Res.string.create_note) })
            .assertIsDisplayed()

    companion object {
        var noteItemTitleText: String = "Lorem"
    }
}