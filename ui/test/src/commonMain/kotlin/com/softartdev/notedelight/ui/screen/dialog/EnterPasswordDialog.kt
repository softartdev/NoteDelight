package com.softartdev.notedelight.ui.screen.dialog

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.onNodeWithTag
import com.softartdev.notedelight.ui.dialog.security.ENTER_PASSWORD_DIALOG_FIELD_TAG
import com.softartdev.notedelight.ui.dialog.security.ENTER_PASSWORD_DIALOG_LABEL_TAG
import com.softartdev.notedelight.ui.dialog.security.ENTER_PASSWORD_DIALOG_SAVE_BUTTON_TAG
import com.softartdev.notedelight.ui.dialog.security.ENTER_PASSWORD_DIALOG_TAG
import com.softartdev.notedelight.ui.dialog.security.ENTER_PASSWORD_DIALOG_VISIBILITY_TAG
import kotlin.jvm.JvmInline

@JvmInline
value class EnterPasswordDialog(val commonDialog: CommonDialog) : CommonDialog by commonDialog {

    val dialogSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(ENTER_PASSWORD_DIALOG_TAG, useUnmergedTree = true)

    val enterPasswordSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(ENTER_PASSWORD_DIALOG_FIELD_TAG, useUnmergedTree = true)

    val enterLabelSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(ENTER_PASSWORD_DIALOG_LABEL_TAG, useUnmergedTree = true)

    val enterVisibilitySNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(ENTER_PASSWORD_DIALOG_VISIBILITY_TAG, useUnmergedTree = true)

    override val confirmDialogButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(ENTER_PASSWORD_DIALOG_SAVE_BUTTON_TAG, useUnmergedTree = true)
}