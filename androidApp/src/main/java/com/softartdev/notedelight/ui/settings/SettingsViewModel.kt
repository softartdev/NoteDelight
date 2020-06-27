package com.softartdev.notedelight.ui.settings

import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.ui.base.BaseViewModel


class SettingsViewModel (
        private val cryptUseCase: CryptUseCase
) : BaseViewModel<SecurityResult>() {

    fun checkEncryption() = launch {
        val isEncrypted = cryptUseCase.dbIsEncrypted()
        SecurityResult.EncryptEnable(isEncrypted)
    }

    fun changeEncryption(checked: Boolean) = launch {
        when (checked) {
            true -> SecurityResult.SetPasswordDialog
            false -> when (cryptUseCase.dbIsEncrypted()) {
                true -> SecurityResult.PasswordDialog
                false -> SecurityResult.EncryptEnable(false)
            }
        }
    }

    fun changePassword() = launch {
            when(cryptUseCase.dbIsEncrypted()) {
                true -> SecurityResult.ChangePasswordDialog
                false -> SecurityResult.SetPasswordDialog
            }
        }

    override fun errorResult(throwable: Throwable): SecurityResult = SecurityResult.Error(throwable.message)
}
