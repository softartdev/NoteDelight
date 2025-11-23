package com.softartdev.notedelight.presentation.settings

import com.softartdev.notedelight.model.LanguageEnum

data class SecurityResult(
    val loading: Boolean = false,
    val encryption: Boolean = false,
    val fileListVisible: Boolean = false,
    val language: LanguageEnum = LanguageEnum.ENGLISH,
) {
    fun showLoading(): SecurityResult = copy(loading = true)
    fun hideLoading(): SecurityResult = copy(loading = false)
    fun hideEncryption(): SecurityResult = copy(encryption = false)
    fun showFileList(): SecurityResult = copy(fileListVisible = true)
}

sealed interface SettingsAction {
    data object NavBack : SettingsAction
    data object ChangeTheme : SettingsAction
    data object ChangeLanguage : SettingsAction
    data object CheckEncryption : SettingsAction
    data class ChangeEncryption(val checked: Boolean) : SettingsAction
    data object ChangePassword : SettingsAction
    data object ShowCipherVersion : SettingsAction
    data object ShowDatabasePath : SettingsAction
    data object ShowFileList : SettingsAction
    data object RevealFileList : SettingsAction
}
