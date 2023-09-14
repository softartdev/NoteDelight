package com.softartdev.notedelight.ui.screen.dialog

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithText
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.MR

@JvmInline
value class CommonDialogImpl(override val composeTestRule: ComposeContentTestRule): CommonDialog {

    override val yesDialogButtonSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithText(text = MR.strings.yes.contextLocalized())
            .assertIsDisplayed()
}