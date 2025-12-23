package com.softartdev.notedelight.ui.screen

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.softartdev.notedelight.util.runBlockingAll
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.create_note
import notedelight.ui.shared.generated.resources.label_empty_result
import notedelight.ui.shared.generated.resources.settings
import org.jetbrains.compose.resources.getString
import kotlin.jvm.JvmInline

@JvmInline
value class MainTestScreen(val nodeProvider: SemanticsNodeInteractionsProvider) {

    val settingsMenuButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithContentDescription(label = runBlockingAll { getString(Res.string.settings) })
            .assertIsDisplayed()

    val emptyResultLabelSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithText(text = runBlockingAll { getString(Res.string.label_empty_result) })

    val noteListItemSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithContentDescription(label = noteItemTitleText)

    val labelEmptyResultSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithText(text = runBlockingAll { getString(Res.string.label_empty_result) })

    val fabSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithContentDescription(label = runBlockingAll { getString(Res.string.create_note) })
            .assertIsDisplayed()

    companion object {
        var noteItemTitleText: String = "Lorem"
    }
}