package com.softartdev.notedelight.ui.screen.dialog

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import kotlinx.coroutines.runBlocking
import notedelight.shared.generated.resources.Res
import notedelight.shared.generated.resources.enter_title
import org.jetbrains.compose.resources.getString

@JvmInline
value class EditTitleDialog(val commonDialog: CommonDialog) : CommonDialog by commonDialog {

    val editTitleSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithContentDescription(label = runBlocking { getString(Res.string.enter_title) })
            .assertIsDisplayed()
}