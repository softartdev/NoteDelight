package com.softartdev.notedelight.util

import co.touchlab.kermit.Logger
import co.touchlab.kermit.koin.KermitKoinLogger
import com.softartdev.notedelight.model.LanguageEnum
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.en_lang
import notedelight.ui.shared.generated.resources.ru_lang
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

const val DEFAULT_APP_LOG_TAG = "NOTE_DELIGHT_APP"

/**
 * Setup Kermit Logger for Koin
 * @param level
 */
fun KoinApplication.kermitLogger(level: Level = Level.DEBUG): KoinApplication = logger(
    logger = KermitKoinLogger(Logger.withTag(KOIN_TAG)).also { it.level = level }
)
