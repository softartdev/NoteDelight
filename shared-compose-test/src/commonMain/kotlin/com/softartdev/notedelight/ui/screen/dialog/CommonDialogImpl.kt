@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui.screen.dialog

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.MR
import kotlin.jvm.JvmInline

@JvmInline
value class CommonDialogImpl(override val composeTestRule: ComposeUiTest): CommonDialog {

    override val yesDialogButtonSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithText(text = MR.strings.yes.contextLocalized())
            .assertIsDisplayed()
}