package com.softartdev.notedelight.ui.screen

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.softartdev.notedelight.model.NoteSort
import com.softartdev.notedelight.util.CREATE_NOTE_FAB_TAG
import com.softartdev.notedelight.util.EMPTY_RESULT_LABEL_TAG
import com.softartdev.notedelight.util.MAIN_SETTINGS_BUTTON_TAG
import com.softartdev.notedelight.util.MAIN_SORT_BUTTON_TAG
import com.softartdev.notedelight.util.sortOptionTestTag
import kotlin.jvm.JvmInline

@JvmInline
value class MainTestScreen(val nodeProvider: SemanticsNodeInteractionsProvider) {

    val sortMenuButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithTag(MAIN_SORT_BUTTON_TAG)
            .assertIsDisplayed()

    fun sortOptionSNI(noteSort: NoteSort): SemanticsNodeInteraction = nodeProvider
        .onNodeWithTag(sortOptionTestTag(noteSort))
        .assertIsDisplayed()

    fun noteItemSNI(title: String): SemanticsNodeInteraction = nodeProvider
        .onNodeWithContentDescription(label = title)

    fun noteDateSNI(formattedDate: String): SemanticsNodeInteraction = nodeProvider
        .onNodeWithText(text = formattedDate, useUnmergedTree = true)

    fun noteDatesSNIC(formattedDate: String): SemanticsNodeInteractionCollection = nodeProvider
        .onAllNodesWithText(text = formattedDate, useUnmergedTree = true)

    val settingsMenuButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithTag(MAIN_SETTINGS_BUTTON_TAG)
            .assertIsDisplayed()

    val emptyResultLabelSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(EMPTY_RESULT_LABEL_TAG)

    val noteListItemSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithContentDescription(label = noteItemTitleText)

    val labelEmptyResultSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(EMPTY_RESULT_LABEL_TAG)

    val fabSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithTag(CREATE_NOTE_FAB_TAG)
            .assertIsDisplayed()

    companion object {
        var noteItemTitleText: String = "Lorem"
    }
}