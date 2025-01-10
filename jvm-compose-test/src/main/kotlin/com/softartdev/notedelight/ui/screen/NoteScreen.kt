package com.softartdev.notedelight.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import kotlinx.coroutines.runBlocking
import notedelight.shared.generated.resources.Res
import notedelight.shared.generated.resources.action_delete_note
import notedelight.shared.generated.resources.action_edit_title
import notedelight.shared.generated.resources.action_save_note
import notedelight.shared.generated.resources.type_text
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
            .onNodeWithText(text = runBlocking { getString(Res.string.type_text) })
            .assertIsDisplayed()
}
