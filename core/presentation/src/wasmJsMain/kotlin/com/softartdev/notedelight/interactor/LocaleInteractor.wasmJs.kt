package com.softartdev.notedelight.interactor

import com.softartdev.notedelight.model.LanguageEnum

external object window {
    var __customLocale: String?
}

actual class LocaleInteractor {
    actual var languageEnum: LanguageEnum
        get() {
            val currentLang: String? = window.__customLocale
            val langEnum: LanguageEnum? = LanguageEnum.entries.find { enum ->
                currentLang?.equals(enum.locale, ignoreCase = true) == true
            }
            return langEnum ?: LanguageEnum.ENGLISH
        }
        set(value) {
            window.__customLocale = value.locale
        }
}
