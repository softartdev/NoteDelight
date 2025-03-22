package com.softartdev.notedelight.ui.screen

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsToggleable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import kotlinx.coroutines.runBlocking
import notedelight.shared.generated.resources.Res
import notedelight.shared.generated.resources.pref_title_enable_encryption
import notedelight.shared.generated.resources.pref_title_set_password
import org.jetbrains.compose.resources.getString

@JvmInline
value class SettingsTestScreen(val composeTestRule: ComposeContentTestRule) {

    val encryptionSwitchSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithTag(testTag = runBlocking { getString(Res.string.pref_title_enable_encryption) })
            .assertIsToggleable()
            .assertIsDisplayed()

    val setPasswordSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithContentDescription(runBlocking { getString(Res.string.pref_title_set_password) })
            .assertIsDisplayed()
}