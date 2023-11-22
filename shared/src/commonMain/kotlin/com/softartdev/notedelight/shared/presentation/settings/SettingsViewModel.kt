package com.softartdev.notedelight.shared.presentation.settings

import com.softartdev.notedelight.shared.PlatformSQLiteState
import com.softartdev.notedelight.shared.base.BaseViewModel
import com.softartdev.notedelight.shared.db.SafeRepo
import com.softartdev.notedelight.shared.usecase.crypt.CheckSqlCipherVersionUseCase


class SettingsViewModel(
    private val safeRepo: SafeRepo,
    private val checkSqlCipherVersionUseCase: CheckSqlCipherVersionUseCase
) : BaseViewModel<SecurityResult>() {

    override val loadingResult: SecurityResult = SecurityResult.Loading

    private val dbIsEncrypted: Boolean
        get() = safeRepo.databaseState == PlatformSQLiteState.ENCRYPTED

    fun checkEncryption() = launch {
        SecurityResult.EncryptEnable(dbIsEncrypted)
    }

    fun changeEncryption(checked: Boolean) = launch {
        when (checked) {
            true -> SecurityResult.SetPasswordDialog
            false -> when {
                dbIsEncrypted -> SecurityResult.PasswordDialog
                else -> SecurityResult.EncryptEnable(false)
            }
        }
    }

    fun changePassword() = launch {
        when {
            dbIsEncrypted -> SecurityResult.ChangePasswordDialog
            else -> SecurityResult.SetPasswordDialog
        }
    }

    fun showCipherVersion() = launch {
        val cipherVersion: String? = checkSqlCipherVersionUseCase.invoke()
        SecurityResult.SnackBar(cipherVersion)
    }

    override fun errorResult(throwable: Throwable): SecurityResult =
        SecurityResult.Error(throwable.message)
}
