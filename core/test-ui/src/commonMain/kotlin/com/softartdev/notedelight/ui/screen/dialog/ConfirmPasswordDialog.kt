package com.softartdev.notedelight.ui.screen.dialog

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.onNodeWithTag
import com.softartdev.notedelight.util.CONFIRM_PASSWORD_DIALOG_FIELD_TAG
import com.softartdev.notedelight.util.CONFIRM_PASSWORD_DIALOG_LABEL_TAG
import com.softartdev.notedelight.util.CONFIRM_PASSWORD_DIALOG_REPEAT_FIELD_TAG
import com.softartdev.notedelight.util.CONFIRM_PASSWORD_DIALOG_REPEAT_LABEL_TAG
import com.softartdev.notedelight.util.CONFIRM_PASSWORD_DIALOG_REPEAT_VISIBILITY_TAG
import com.softartdev.notedelight.util.CONFIRM_PASSWORD_DIALOG_SAVE_BUTTON_TAG
import com.softartdev.notedelight.util.CONFIRM_PASSWORD_DIALOG_TAG
import com.softartdev.notedelight.util.CONFIRM_PASSWORD_DIALOG_VISIBILITY_TAG
import kotlin.jvm.JvmInline

@JvmInline
value class ConfirmPasswordDialog(val commonDialog: CommonDialog): CommonDialog by commonDialog {

    val dialogSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(CONFIRM_PASSWORD_DIALOG_TAG, useUnmergedTree = true)

    val confirmPasswordSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(CONFIRM_PASSWORD_DIALOG_FIELD_TAG, useUnmergedTree = true)

    val confirmLabelSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(CONFIRM_PASSWORD_DIALOG_LABEL_TAG, useUnmergedTree = true)

    val confirmVisibilitySNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(CONFIRM_PASSWORD_DIALOG_VISIBILITY_TAG, useUnmergedTree = true)

    val confirmRepeatPasswordSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(CONFIRM_PASSWORD_DIALOG_REPEAT_FIELD_TAG, useUnmergedTree = true)

    val confirmRepeatLabelSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(CONFIRM_PASSWORD_DIALOG_REPEAT_LABEL_TAG, useUnmergedTree = true)

    val confirmRepeatVisibilitySNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(CONFIRM_PASSWORD_DIALOG_REPEAT_VISIBILITY_TAG, useUnmergedTree = true)

    override val confirmDialogButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(CONFIRM_PASSWORD_DIALOG_SAVE_BUTTON_TAG, useUnmergedTree = true)
}