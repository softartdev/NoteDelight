package com.softartdev.notedelight.presentation.settings

import androidx.lifecycle.ViewModel
import com.softartdev.notedelight.interactor.LocaleInteractor
import com.softartdev.notedelight.model.LanguageEnum
import com.softartdev.notedelight.navigation.Router
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LanguageViewModel(
    private val router: Router,
    private val localeInteractor: LocaleInteractor,
) : ViewModel() {
    val selectedLanguage: StateFlow<LanguageEnum>
        field = MutableStateFlow(localeInteractor.languageEnum)

    fun selectLanguage(language: LanguageEnum) {
        localeInteractor.languageEnum = language
        selectedLanguage.value = language
    }

    fun dismiss() = router.popBackStack()
}
