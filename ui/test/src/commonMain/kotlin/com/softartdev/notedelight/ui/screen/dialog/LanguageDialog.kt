package com.softartdev.notedelight.ui.screen.dialog

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.onNodeWithTag
import com.softartdev.notedelight.model.LanguageEnum
import com.softartdev.notedelight.util.CHOOSE_LANGUAGE_DIALOG_TITLE_TAG
import com.softartdev.notedelight.util.OK_BUTTON_TAG
import com.softartdev.notedelight.util.testTag
import kotlin.jvm.JvmInline

@JvmInline
value class LanguageDialog(val commonDialog: CommonDialog) : CommonDialog by commonDialog {

    val langDialogTitleSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(CHOOSE_LANGUAGE_DIALOG_TITLE_TAG)

    val LanguageEnum.radioButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(testTag = this.testTag)

    override val confirmDialogButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(OK_BUTTON_TAG)
}

