package com.softartdev.notedelight.shared.presentation.settings.security.change

import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.shared.base.BaseViewModel


class ChangeViewModel (
        private val cryptUseCase: CryptUseCase
) : BaseViewModel<ChangeResult>() {

    override var initResult: ChangeResult? = ChangeResult.InitState
    override val loadingResult: ChangeResult = ChangeResult.Loading

    fun checkChange(
            oldPassword: CharSequence,
            newPassword: CharSequence,
            repeatNewPassword: CharSequence
    ) = launch {
        when {
            oldPassword.isEmpty() -> ChangeResult.OldEmptyPasswordError
            newPassword.isEmpty() -> ChangeResult.NewEmptyPasswordError
            newPassword.toString() != repeatNewPassword.toString() -> ChangeResult.PasswordsNoMatchError
            else -> when (cryptUseCase.checkPassword(oldPassword)) {
                true -> {
                    cryptUseCase.changePassword(oldPassword, newPassword)
                    ChangeResult.Success
                }
                false -> ChangeResult.IncorrectPasswordError
            }
        }
    }

    override fun errorResult(throwable: Throwable): ChangeResult = ChangeResult.Error(throwable.message)
}
