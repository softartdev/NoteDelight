package com.softartdev.notedelight.presentation.settings

data class SecurityResult(
    val loading: Boolean = false,
    val encryption: Boolean = false,
    val snackBarMessage: String? = null,
    val navBack: () -> Unit = {},
    val changeTheme: () -> Unit = {},
    val checkEncryption: () -> Unit = {},
    val changeEncryption: (Boolean) -> Unit = {},
    val changePassword: () -> Unit = {},
    val showCipherVersion: () -> Unit = {},
    val showDatabasePath: () -> Unit = {},
    val disposeOneTimeEvents: () -> Unit = {},
) {
    fun showLoading(): SecurityResult = copy(loading = true)
    fun hideLoading(): SecurityResult = copy(loading = false)
    fun hideEncryption(): SecurityResult = copy(encryption = false)
    fun hideSnackBarMessage(): SecurityResult = copy(snackBarMessage = null)
}
