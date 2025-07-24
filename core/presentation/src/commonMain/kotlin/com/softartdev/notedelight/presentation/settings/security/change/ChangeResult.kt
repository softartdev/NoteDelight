package com.softartdev.notedelight.presentation.settings.security.change

import com.softartdev.notedelight.presentation.settings.security.FieldLabel

data class ChangeResult(
    val loading: Boolean = false,
    // Old password field
    val oldPassword: String = "",
    val oldPasswordFieldLabel: FieldLabel = FieldLabel.ENTER_OLD_PASSWORD,
    val isOldPasswordError: Boolean = false,
    // New password field
    val newPassword: String = "",
    val newPasswordFieldLabel: FieldLabel = FieldLabel.ENTER_NEW_PASSWORD,
    val isNewPasswordError: Boolean = false,
    // Repeat new password field
    val repeatNewPassword: String = "",
    val repeatPasswordFieldLabel: FieldLabel = FieldLabel.REPEAT_NEW_PASSWORD,
    val isRepeatPasswordError: Boolean = false,
    // Common
    val snackBarMessageType: String? = null,
    // Events
    val onCancel: () -> Unit = {},
    val onEditOldPassword: (String) -> Unit = {},
    val onEditNewPassword: (String) -> Unit = {},
    val onEditRepeatPassword: (String) -> Unit = {},
    val onChangeClick: () -> Unit = {},
    val disposeOneTimeEvents: () -> Unit = {}
) {
    fun showLoading(): ChangeResult = copy(loading = true)
    fun hideLoading(): ChangeResult = copy(loading = false)
    fun hideErrors(): ChangeResult = copy(
        isOldPasswordError = false,
        isNewPasswordError = false,
        isRepeatPasswordError = false,
        oldPasswordFieldLabel = FieldLabel.ENTER_OLD_PASSWORD,
        newPasswordFieldLabel = FieldLabel.ENTER_NEW_PASSWORD,
        repeatPasswordFieldLabel = FieldLabel.REPEAT_NEW_PASSWORD
    )
    fun hideSnackBarMessage(): ChangeResult = copy(snackBarMessageType = null)
}
