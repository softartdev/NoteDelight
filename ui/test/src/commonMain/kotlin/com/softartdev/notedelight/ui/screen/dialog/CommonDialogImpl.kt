package com.softartdev.notedelight.ui.screen.dialog

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onNodeWithText
import com.softartdev.notedelight.util.runBlockingAll
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.yes
import org.jetbrains.compose.resources.getString
import kotlin.jvm.JvmInline

@JvmInline
value class CommonDialogImpl(override val nodeProvider: SemanticsNodeInteractionsProvider): CommonDialog {

    override val confirmDialogButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithText(text = runBlockingAll { getString(Res.string.yes) })
}