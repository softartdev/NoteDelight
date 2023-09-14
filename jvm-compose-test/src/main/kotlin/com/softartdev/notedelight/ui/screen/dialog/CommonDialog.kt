package com.softartdev.notedelight.ui.screen.dialog

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.ComposeContentTestRule

interface CommonDialog {
    val composeTestRule: ComposeContentTestRule
    val yesDialogButtonSNI: SemanticsNodeInteraction
}