package com.softartdev.notedelight.presentation.settings

data class SecurityResult(
    val loading: Boolean = false,
    val encryption: Boolean = false,
) {
    fun showLoading(): SecurityResult = copy(loading = true)
    fun hideLoading(): SecurityResult = copy(loading = false)
    fun hideEncryption(): SecurityResult = copy(encryption = false)
}

sealed interface SettingsAction {
    data object NavBack : SettingsAction
    data object ChangeTheme : SettingsAction
    data object CheckEncryption : SettingsAction
    data class ChangeEncryption(val checked: Boolean) : SettingsAction
    data object ChangePassword : SettingsAction
    data object ShowCipherVersion : SettingsAction
    data object ShowDatabasePath : SettingsAction
}
