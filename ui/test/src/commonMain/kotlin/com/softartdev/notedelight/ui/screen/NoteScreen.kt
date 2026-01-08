package com.softartdev.notedelight.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import com.softartdev.notedelight.ui.main.NOTE_TEXT_FIELD_TAG
import com.softartdev.notedelight.util.DELETE_NOTE_BUTTON_TAG
import com.softartdev.notedelight.util.EDIT_TITLE_BUTTON_TAG
import com.softartdev.notedelight.util.SAVE_NOTE_BUTTON_TAG
import kotlin.jvm.JvmInline

@JvmInline
value class NoteScreen(val nodeProvider: SemanticsNodeInteractionsProvider) {

    val backButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithContentDescription(label = Icons.AutoMirrored.Filled.ArrowBack.name)
            .assertIsDisplayed()

    val saveNoteMenuButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithTag(SAVE_NOTE_BUTTON_TAG)
            .assertIsDisplayed()

    val editTitleMenuButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithTag(EDIT_TITLE_BUTTON_TAG)
            .assertIsDisplayed()

    val deleteNoteMenuButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithTag(DELETE_NOTE_BUTTON_TAG)
            .assertIsDisplayed()

    val textFieldSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithTag(testTag = NOTE_TEXT_FIELD_TAG)
            .assertIsDisplayed()
}
