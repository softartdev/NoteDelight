package com.softartdev.notedelight.ui.screen.dialog

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.onNodeWithTag
import com.softartdev.notedelight.util.CANCEL_BUTTON_TAG
import com.softartdev.notedelight.util.NO_BUTTON_TAG
import com.softartdev.notedelight.util.SAVE_NOTE_DIALOG_TAG
import kotlin.jvm.JvmInline

@JvmInline
value class SaveDialog(val commonDialog: CommonDialog) : CommonDialog by commonDialog {

    val dialogSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(SAVE_NOTE_DIALOG_TAG)

    val cancelButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(CANCEL_BUTTON_TAG)

    val noButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(NO_BUTTON_TAG)

    val yesButtonSNI: SemanticsNodeInteraction
        get() = confirmDialogButtonSNI
}