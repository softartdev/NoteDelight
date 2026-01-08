package com.softartdev.notedelight.ui.screen

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsToggleable
import androidx.compose.ui.test.onNodeWithTag
import com.softartdev.notedelight.util.ENABLE_ENCRYPTION_SWITCH_TAG
import com.softartdev.notedelight.util.LANGUAGE_BUTTON_TAG
import com.softartdev.notedelight.util.SET_PASSWORD_BUTTON_TAG
import kotlin.jvm.JvmInline

@JvmInline
value class SettingsTestScreen(val nodeProvider: SemanticsNodeInteractionsProvider) {

    val encryptionSwitchSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithTag(ENABLE_ENCRYPTION_SWITCH_TAG)
            .assertIsToggleable()
            .assertIsDisplayed()

    val setPasswordSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithTag(SET_PASSWORD_BUTTON_TAG)
            .assertIsDisplayed()

    val languageSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithTag(LANGUAGE_BUTTON_TAG)
            .assertIsDisplayed()
}