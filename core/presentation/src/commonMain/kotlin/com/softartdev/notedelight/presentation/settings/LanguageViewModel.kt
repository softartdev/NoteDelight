package com.softartdev.notedelight.presentation.settings

import androidx.lifecycle.ViewModel
import com.softartdev.notedelight.interactor.LocaleInteractor
import com.softartdev.notedelight.model.LanguageEnum
import com.softartdev.notedelight.navigation.Router
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LanguageViewModel(
    private val router: Router,
    private val localeInteractor: LocaleInteractor,
) : ViewModel() {
    private val mutableStateFlow = MutableStateFlow(localeInteractor.languageEnum)
    val selectedLanguage: StateFlow<LanguageEnum> = mutableStateFlow.asStateFlow()
    
    fun selectLanguage(language: LanguageEnum) {
        localeInteractor.languageEnum = language
        mutableStateFlow.value = language
    }
    
    fun dismiss() = router.popBackStack()
}
