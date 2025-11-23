package com.softartdev.notedelight.ui.screen.dialog

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.softartdev.notedelight.model.LanguageEnum
import com.softartdev.notedelight.util.testTag
import io.github.softartdev.theme_prefs.generated.resources.ok
import kotlinx.coroutines.runBlocking
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.choose_language
import org.jetbrains.compose.resources.getString
import io.github.softartdev.theme_prefs.generated.resources.Res as ThemePrefsRes

@JvmInline
value class LanguageDialog(val commonDialog: CommonDialog) : CommonDialog by commonDialog {

    val langDialogTitleSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithText(text = runBlocking { getString(Res.string.choose_language) })

    val LanguageEnum.radioButtonSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithTag(testTag = this.testTag)

    override val yesDialogButtonSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithText(text = runBlocking { getString(ThemePrefsRes.string.ok) })
}

