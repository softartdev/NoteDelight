package com.softartdev.notedelight.presentation.settings

import com.softartdev.notedelight.model.LanguageEnum
import com.softartdev.notedelight.model.SettingsCategory

data class SettingsResult(
    val loading: Boolean = false,
    val encryption: Boolean = false,
    val fileListVisible: Boolean = false,
    val language: LanguageEnum = LanguageEnum.ENGLISH,
    val appVersion: String? = null,
    val selectedCategory: SettingsCategory? = null,
) {
    fun showLoading(): SettingsResult = copy(loading = true)
    fun hideLoading(): SettingsResult = copy(loading = false)
    fun hideEncryption(): SettingsResult = copy(encryption = false)
    fun showFileList(): SettingsResult = copy(fileListVisible = true)
}

sealed interface SettingsAction {
    data object NavBack : SettingsAction
    data object Refresh : SettingsAction
    data object ChangeTheme : SettingsAction
    data object ChangeLanguage : SettingsAction
    data class ChangeEncryption(val checked: Boolean) : SettingsAction
    data object ChangePassword : SettingsAction
    data object ShowCipherVersion : SettingsAction
    data object ShowDatabasePath : SettingsAction
    data class ExportDatabase(val destinationPath: String?) : SettingsAction
    data class ImportDatabase(val sourcePath: String?) : SettingsAction
    data object ShowFileList : SettingsAction
    data object RevealFileList : SettingsAction
}
