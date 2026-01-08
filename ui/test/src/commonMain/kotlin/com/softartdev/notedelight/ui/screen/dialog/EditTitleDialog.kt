package com.softartdev.notedelight.ui.screen.dialog

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import com.softartdev.notedelight.util.ENTER_TITLE_DIALOG_TAG
import kotlin.jvm.JvmInline

@JvmInline
value class EditTitleDialog(val commonDialog: CommonDialog) : CommonDialog by commonDialog {

    val editTitleSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithTag(ENTER_TITLE_DIALOG_TAG)
            .assertIsDisplayed()
}