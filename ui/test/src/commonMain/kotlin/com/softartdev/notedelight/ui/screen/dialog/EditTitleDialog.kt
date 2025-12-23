package com.softartdev.notedelight.ui.screen.dialog

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import com.softartdev.notedelight.util.runBlockingAll
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.enter_title
import org.jetbrains.compose.resources.getString
import kotlin.jvm.JvmInline

@JvmInline
value class EditTitleDialog(val commonDialog: CommonDialog) : CommonDialog by commonDialog {

    val editTitleSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithContentDescription(label = runBlockingAll { getString(Res.string.enter_title) })
            .assertIsDisplayed()
}