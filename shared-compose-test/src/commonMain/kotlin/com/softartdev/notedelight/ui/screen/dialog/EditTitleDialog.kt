@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui.screen.dialog

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.MR
import kotlin.jvm.JvmInline

@JvmInline
value class EditTitleDialog(val commonDialog: CommonDialog) : CommonDialog by commonDialog {

    val editTitleSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithContentDescription(label = MR.strings.enter_title.contextLocalized())
            .assertIsDisplayed()
}