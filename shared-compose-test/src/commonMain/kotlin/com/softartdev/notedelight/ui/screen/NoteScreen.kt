@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.MR
import kotlin.jvm.JvmInline

@JvmInline
value class NoteScreen(val composeTestRule: ComposeUiTest) {

    val backButtonSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithContentDescription(label = Icons.AutoMirrored.Filled.ArrowBack.name)
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
