package com.softartdev.notedelight.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import com.softartdev.notedelight.ui.main.NOTE_TEXT_FIELD_TAG
import kotlinx.coroutines.runBlocking
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.action_delete_note
import notedelight.ui.shared.generated.resources.action_edit_title
import notedelight.ui.shared.generated.resources.action_save_note
import org.jetbrains.compose.resources.getString

@JvmInline
value class NoteScreen(val composeTestRule: ComposeContentTestRule) {

    val backButtonSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithContentDescription(label = Icons.AutoMirrored.Filled.ArrowBack.name)
            .assertIsDisplayed()

    val saveNoteMenuButtonSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithContentDescription(label = runBlocking { getString(Res.string.action_save_note) })
            .assertIsDisplayed()

    val editTitleMenuButtonSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithContentDescription(label = runBlocking { getString(Res.string.action_edit_title) })
            .assertIsDisplayed()

    val deleteNoteMenuButtonSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithContentDescription(label = runBlocking { getString(Res.string.action_delete_note) })
            .assertIsDisplayed()

    val textFieldSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithTag(testTag = NOTE_TEXT_FIELD_TAG)
            .assertIsDisplayed()
}
