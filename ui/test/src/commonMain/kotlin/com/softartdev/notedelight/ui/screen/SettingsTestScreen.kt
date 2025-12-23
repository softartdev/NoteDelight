package com.softartdev.notedelight.ui.screen

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsToggleable
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.softartdev.notedelight.util.runBlockingAll
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.language
import notedelight.ui.shared.generated.resources.pref_title_enable_encryption
import notedelight.ui.shared.generated.resources.pref_title_set_password
import org.jetbrains.compose.resources.getString
import kotlin.jvm.JvmInline

@JvmInline
value class SettingsTestScreen(val nodeProvider: SemanticsNodeInteractionsProvider) {

    val encryptionSwitchSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithTag(testTag = runBlockingAll { getString(Res.string.pref_title_enable_encryption) })
            .assertIsToggleable()
            .assertIsDisplayed()

    val setPasswordSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithContentDescription(runBlockingAll { getString(Res.string.pref_title_set_password) })
            .assertIsDisplayed()

    val languageSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithText(text = runBlockingAll { getString(Res.string.language) })
            .assertIsDisplayed()
}