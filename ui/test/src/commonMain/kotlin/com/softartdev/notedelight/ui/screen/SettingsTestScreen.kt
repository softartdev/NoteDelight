package com.softartdev.notedelight.ui.screen

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsToggleable
import androidx.compose.ui.test.onNodeWithTag
import com.softartdev.notedelight.util.ENABLE_ENCRYPTION_SWITCH_TAG
import com.softartdev.notedelight.util.EXPORT_DATABASE_BUTTON_TAG
import com.softartdev.notedelight.util.IMPORT_DATABASE_BUTTON_TAG
import com.softartdev.notedelight.util.LANGUAGE_BUTTON_TAG
import com.softartdev.notedelight.util.SET_PASSWORD_BUTTON_TAG
import com.softartdev.notedelight.util.SETTINGS_CATEGORY_APPEARANCE_TAG
import com.softartdev.notedelight.util.SETTINGS_CATEGORY_BACKUP_TAG
import com.softartdev.notedelight.util.SETTINGS_CATEGORY_INFO_TAG
import com.softartdev.notedelight.util.SETTINGS_CATEGORY_SECURITY_TAG
import kotlin.jvm.JvmInline

@JvmInline
value class SettingsTestScreen(val nodeProvider: SemanticsNodeInteractionsProvider) {

    val appearanceCategorySNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithTag(SETTINGS_CATEGORY_APPEARANCE_TAG)
            .assertIsDisplayed()

    val securityCategorySNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithTag(SETTINGS_CATEGORY_SECURITY_TAG)
            .assertIsDisplayed()

    val backupCategorySNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithTag(SETTINGS_CATEGORY_BACKUP_TAG)
            .assertIsDisplayed()

    val infoCategorySNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithTag(SETTINGS_CATEGORY_INFO_TAG)
            .assertIsDisplayed()

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

    val exportDatabaseSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithTag(EXPORT_DATABASE_BUTTON_TAG)
            .assertIsDisplayed()

    val importDatabaseSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithTag(IMPORT_DATABASE_BUTTON_TAG)
            .assertIsDisplayed()
}
