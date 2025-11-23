package com.softartdev.notedelight.interactor

import com.softartdev.notedelight.model.LanguageEnum
import java.util.Locale

actual class LocaleInteractor {
    actual var languageEnum: LanguageEnum
        get() {
            val locale: String = Locale.getDefault().language
            val langEnum: LanguageEnum? = LanguageEnum.entries.find { enum ->
                locale.equals(enum.locale, ignoreCase = true)
            }
            return langEnum ?: LanguageEnum.ENGLISH
        }
        set(value) {
            val locale = Locale(value.locale)
            Locale.setDefault(locale)
        }
}
