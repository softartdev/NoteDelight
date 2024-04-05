@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui.screen.dialog

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction

interface CommonDialog {
    val composeTestRule: ComposeUiTest
    val yesDialogButtonSNI: SemanticsNodeInteraction
}