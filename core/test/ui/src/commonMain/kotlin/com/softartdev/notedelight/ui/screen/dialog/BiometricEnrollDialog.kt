package com.softartdev.notedelight.ui.screen.dialog

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.onNodeWithTag
import com.softartdev.notedelight.util.BIOMETRIC_ENROLL_DIALOG_FIELD_TAG
import com.softartdev.notedelight.util.BIOMETRIC_ENROLL_DIALOG_LABEL_TAG
import com.softartdev.notedelight.util.BIOMETRIC_ENROLL_DIALOG_SAVE_BUTTON_TAG
import com.softartdev.notedelight.util.BIOMETRIC_ENROLL_DIALOG_TAG
import com.softartdev.notedelight.util.BIOMETRIC_ENROLL_DIALOG_VISIBILITY_TAG
import kotlin.jvm.JvmInline

@JvmInline
value class BiometricEnrollDialog(
    val commonDialog: CommonDialog,
) : CommonDialog by commonDialog {

    val dialogSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(BIOMETRIC_ENROLL_DIALOG_TAG, useUnmergedTree = true)

    val passwordSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(BIOMETRIC_ENROLL_DIALOG_FIELD_TAG, useUnmergedTree = true)

    val labelSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(BIOMETRIC_ENROLL_DIALOG_LABEL_TAG, useUnmergedTree = true)

    val visibilitySNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(BIOMETRIC_ENROLL_DIALOG_VISIBILITY_TAG, useUnmergedTree = true)

    override val confirmDialogButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(BIOMETRIC_ENROLL_DIALOG_SAVE_BUTTON_TAG, useUnmergedTree = true)
}
