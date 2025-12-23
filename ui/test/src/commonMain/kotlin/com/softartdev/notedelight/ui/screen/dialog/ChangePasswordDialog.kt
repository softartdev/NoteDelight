package com.softartdev.notedelight.ui.screen.dialog

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.onNodeWithTag
import com.softartdev.notedelight.ui.dialog.security.CHANGE_PASSWORD_DIALOG_NEW_FIELD_TAG
import com.softartdev.notedelight.ui.dialog.security.CHANGE_PASSWORD_DIALOG_NEW_LABEL_TAG
import com.softartdev.notedelight.ui.dialog.security.CHANGE_PASSWORD_DIALOG_NEW_VISIBILITY_TAG
import com.softartdev.notedelight.ui.dialog.security.CHANGE_PASSWORD_DIALOG_OLD_FIELD_TAG
import com.softartdev.notedelight.ui.dialog.security.CHANGE_PASSWORD_DIALOG_OLD_LABEL_TAG
import com.softartdev.notedelight.ui.dialog.security.CHANGE_PASSWORD_DIALOG_OLD_VISIBILITY_TAG
import com.softartdev.notedelight.ui.dialog.security.CHANGE_PASSWORD_DIALOG_REPEAT_FIELD_TAG
import com.softartdev.notedelight.ui.dialog.security.CHANGE_PASSWORD_DIALOG_REPEAT_LABEL_TAG
import com.softartdev.notedelight.ui.dialog.security.CHANGE_PASSWORD_DIALOG_REPEAT_VISIBILITY_TAG
import com.softartdev.notedelight.ui.dialog.security.CHANGE_PASSWORD_DIALOG_SAVE_BUTTON_TAG
import com.softartdev.notedelight.ui.dialog.security.CHANGE_PASSWORD_DIALOG_TAG
import kotlin.jvm.JvmInline

@JvmInline
value class ChangePasswordDialog(val commonDialog: CommonDialog) : CommonDialog by commonDialog {

    val dialogSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(CHANGE_PASSWORD_DIALOG_TAG, useUnmergedTree = true)

    val changeOldSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(CHANGE_PASSWORD_DIALOG_OLD_FIELD_TAG, useUnmergedTree = true)

    val changeOldLabelSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(CHANGE_PASSWORD_DIALOG_OLD_LABEL_TAG, useUnmergedTree = true)

    val changeOldVisibilitySNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(CHANGE_PASSWORD_DIALOG_OLD_VISIBILITY_TAG, useUnmergedTree = true)

    val changeNewSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(CHANGE_PASSWORD_DIALOG_NEW_FIELD_TAG, useUnmergedTree = true)

    val changeNewLabelSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(CHANGE_PASSWORD_DIALOG_NEW_LABEL_TAG, useUnmergedTree = true)

    val changeNewVisibilitySNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(CHANGE_PASSWORD_DIALOG_NEW_VISIBILITY_TAG, useUnmergedTree = true)

    val changeRepeatNewSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(CHANGE_PASSWORD_DIALOG_REPEAT_FIELD_TAG, useUnmergedTree = true)

    val changeRepeatLabelSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(CHANGE_PASSWORD_DIALOG_REPEAT_LABEL_TAG, useUnmergedTree = true)

    val changeRepeatNewVisibilitySNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(CHANGE_PASSWORD_DIALOG_REPEAT_VISIBILITY_TAG, useUnmergedTree = true)

    override val confirmDialogButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(CHANGE_PASSWORD_DIALOG_SAVE_BUTTON_TAG, useUnmergedTree = true)
}