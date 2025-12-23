package com.softartdev.notedelight.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import com.softartdev.notedelight.ui.main.NOTE_TEXT_FIELD_TAG
import com.softartdev.notedelight.util.runBlockingAll
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.action_delete_note
import notedelight.ui.shared.generated.resources.action_edit_title
import notedelight.ui.shared.generated.resources.action_save_note
import org.jetbrains.compose.resources.getString
import kotlin.jvm.JvmInline

@JvmInline
value class NoteScreen(val nodeProvider: SemanticsNodeInteractionsProvider) {

    val backButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithContentDescription(label = Icons.AutoMirrored.Filled.ArrowBack.name)
            .assertIsDisplayed()

    val saveNoteMenuButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithContentDescription(label = runBlockingAll { getString(Res.string.action_save_note) })
            .assertIsDisplayed()

    val editTitleMenuButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithContentDescription(label = runBlockingAll { getString(Res.string.action_edit_title) })
            .assertIsDisplayed()

    val deleteNoteMenuButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithContentDescription(label = runBlockingAll { getString(Res.string.action_delete_note) })
            .assertIsDisplayed()

    val textFieldSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithTag(testTag = NOTE_TEXT_FIELD_TAG)
            .assertIsDisplayed()
}
