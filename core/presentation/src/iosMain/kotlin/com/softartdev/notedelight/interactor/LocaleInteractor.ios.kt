package com.softartdev.notedelight.interactor

import com.softartdev.notedelight.model.LanguageEnum
import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.preferredLanguages

actual class LocaleInteractor {
    private val default = NSLocale.preferredLanguages.first() as String

    actual var languageEnum: LanguageEnum
        get() {
            val languages: List<*>? = NSUserDefaults.standardUserDefaults.arrayForKey(LANG_KEY)
            val currentLang: String = languages?.firstOrNull() as? String ?: default
            val langEnum: LanguageEnum? = LanguageEnum.entries.find { enum ->
                currentLang.equals(enum.locale, ignoreCase = true)
            }
            return langEnum ?: LanguageEnum.ENGLISH
        }
        set(value) = NSUserDefaults.standardUserDefaults.setObject(listOf(value.locale), LANG_KEY)

    companion object {
        private const val LANG_KEY = "AppleLanguages"
    }
}
