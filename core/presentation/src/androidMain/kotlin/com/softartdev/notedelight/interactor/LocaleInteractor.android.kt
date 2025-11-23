package com.softartdev.notedelight.interactor

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.softartdev.notedelight.model.LanguageEnum
import java.util.Locale

actual class LocaleInteractor(private val context: Context) {
    actual var languageEnum: LanguageEnum
        get() {
            var applicationLocales: LocaleListCompat = AppCompatDelegate.getApplicationLocales()
            if (applicationLocales.isEmpty && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                applicationLocales = LocaleListCompat.wrap(context.resources.configuration.locales)
            }
            val currentLocale: Locale = applicationLocales.get(0) ?: Locale.getDefault()
            val langEnum: LanguageEnum? = LanguageEnum.entries.find { enum ->
                currentLocale.language.equals(enum.locale, ignoreCase = true)
            }
            return langEnum ?: LanguageEnum.ENGLISH
        }
        set(value) {
            val localeListCompat = LocaleListCompat.forLanguageTags(value.locale)
            AppCompatDelegate.setApplicationLocales(localeListCompat)
        }
}
