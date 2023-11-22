package com.softartdev.notedelight.ui.screen

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsToggleable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.MR

@JvmInline
value class SettingsTestScreen(val composeTestRule: ComposeContentTestRule) {

    val encryptionSwitchSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithTag(testTag = MR.strings.pref_title_enable_encryption.contextLocalized())
            .assertIsToggleable()
            .assertIsDisplayed()

    val setPasswordSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithContentDescription(MR.strings.pref_title_set_password.contextLocalized())
            .assertIsDisplayed()
}