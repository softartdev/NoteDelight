package com.softartdev.notedelight.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.ui.graphics.vector.ImageVector
import co.touchlab.kermit.Logger
import co.touchlab.kermit.koin.KermitKoinLogger
import com.softartdev.notedelight.model.LanguageEnum
import com.softartdev.notedelight.model.SettingsCategory
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.appearance
import notedelight.ui.shared.generated.resources.backup
import notedelight.ui.shared.generated.resources.en_lang
import notedelight.ui.shared.generated.resources.info
import notedelight.ui.shared.generated.resources.ru_lang
import notedelight.ui.shared.generated.resources.security
import org.jetbrains.compose.resources.StringResource
import org.koin.core.KoinApplication
import org.koin.core.logger.KOIN_TAG
import org.koin.core.logger.Level

val LanguageEnum.stringResource: StringResource
    get() = when (this) {
        LanguageEnum.ENGLISH -> Res.string.en_lang
        LanguageEnum.RUSSIAN -> Res.string.ru_lang
    }

val LanguageEnum.testTag: String
    get() = "RADIO_BUTTON_${this.name}_TEST_TAG"

val SettingsCategory.titleRes: StringResource
    get() = when (this) {
        SettingsCategory.Appearance -> Res.string.appearance
        SettingsCategory.Security -> Res.string.security
        SettingsCategory.Backup -> Res.string.backup
        SettingsCategory.Info -> Res.string.info
    }

val SettingsCategory.icon: ImageVector
    get() = when (this) {
        SettingsCategory.Appearance -> Icons.Default.Brightness4
        SettingsCategory.Security -> Icons.Default.Security
        SettingsCategory.Backup -> Icons.Default.Backup
        SettingsCategory.Info -> Icons.Default.Info
    }

val SettingsCategory.tag: String
    get() = when (this) {
        SettingsCategory.Appearance -> SETTINGS_CATEGORY_APPEARANCE_TAG
        SettingsCategory.Security -> SETTINGS_CATEGORY_SECURITY_TAG
        SettingsCategory.Backup -> SETTINGS_CATEGORY_BACKUP_TAG
        SettingsCategory.Info -> SETTINGS_CATEGORY_INFO_TAG
    }

const val DEFAULT_APP_LOG_TAG = "NOTE_DELIGHT_APP"

/**
 * Setup Kermit Logger for Koin
 * @param level
 */
fun KoinApplication.kermitLogger(level: Level = Level.DEBUG): KoinApplication = logger(
    logger = KermitKoinLogger(Logger.withTag(KOIN_TAG)).also { it.level = level }
)
