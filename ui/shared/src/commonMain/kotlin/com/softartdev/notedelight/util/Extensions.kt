package com.softartdev.notedelight.util

import com.softartdev.notedelight.model.LanguageEnum
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.en_lang
import notedelight.ui.shared.generated.resources.ru_lang
import org.jetbrains.compose.resources.StringResource

val LanguageEnum.stringResource: StringResource
    get() = when (this) {
        LanguageEnum.ENGLISH -> Res.string.en_lang
        LanguageEnum.RUSSIAN -> Res.string.ru_lang
    }

val LanguageEnum.testTag: String
    get() = "RADIO_BUTTON_${this.name}_TEST_TAG"
