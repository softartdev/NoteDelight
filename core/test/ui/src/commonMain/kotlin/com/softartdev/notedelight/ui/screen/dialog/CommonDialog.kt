package com.softartdev.notedelight.ui.screen.dialog

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider

interface CommonDialog {
    val nodeProvider: SemanticsNodeInteractionsProvider
    val confirmDialogButtonSNI: SemanticsNodeInteraction
}