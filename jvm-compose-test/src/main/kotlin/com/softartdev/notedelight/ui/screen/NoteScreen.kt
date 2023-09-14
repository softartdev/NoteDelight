package com.softartdev.notedelight.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.MR

@JvmInline
value class NoteScreen(val composeTestRule: ComposeContentTestRule) {

    val backButtonSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithContentDescription(label = Icons.Default.ArrowBack.name)
            .assertIsDisplayed()

    val saveNoteMenuButtonSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithContentDescription(label = MR.strings.action_save_note.contextLocalized())
            .assertIsDisplayed()

    val editTitleMenuButtonSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithContentDescription(label = MR.strings.action_edit_title.contextLocalized())
            .assertIsDisplayed()

    val deleteNoteMenuButtonSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithContentDescription(label = MR.strings.action_delete_note.contextLocalized())
            .assertIsDisplayed()

    val textFieldSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithText(text = MR.strings.type_text.contextLocalized())
            .assertIsDisplayed()
}
