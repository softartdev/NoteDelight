package com.softartdev.notedelight.ui.screen.dialog

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithText
import kotlinx.coroutines.runBlocking
import notedelight.shared.generated.resources.Res
import notedelight.shared.generated.resources.yes
import org.jetbrains.compose.resources.getString

@JvmInline
value class CommonDialogImpl(override val composeTestRule: ComposeContentTestRule): CommonDialog {

    override val yesDialogButtonSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithText(text = runBlocking { getString(Res.string.yes) })
}