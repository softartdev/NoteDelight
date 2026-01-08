package com.softartdev.notedelight.ui.screen.dialog

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onNodeWithTag
import com.softartdev.notedelight.util.YES_BUTTON_TAG
import kotlin.jvm.JvmInline

@JvmInline
value class CommonDialogImpl(override val nodeProvider: SemanticsNodeInteractionsProvider): CommonDialog {

    override val confirmDialogButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(YES_BUTTON_TAG)
}