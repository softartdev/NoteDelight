package com.softartdev.notedelight.ui.screen.dialog

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.onNodeWithTag
import com.softartdev.notedelight.util.BIOMETRIC_DISABLE_CONFIRMATION_DIALOG_TAG
import com.softartdev.notedelight.util.CANCEL_BUTTON_TAG
import kotlin.jvm.JvmInline

@JvmInline
value class BiometricDisableConfirmationDialog(
    val commonDialog: CommonDialog,
) : CommonDialog by commonDialog {

    val dialogSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(BIOMETRIC_DISABLE_CONFIRMATION_DIALOG_TAG)

    val cancelDialogButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(CANCEL_BUTTON_TAG)
}
