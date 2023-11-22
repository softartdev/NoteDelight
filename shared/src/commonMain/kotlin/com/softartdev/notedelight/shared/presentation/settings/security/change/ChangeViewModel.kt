package com.softartdev.notedelight.shared.presentation.settings.security.change

import com.softartdev.notedelight.shared.base.BaseViewModel
import com.softartdev.notedelight.shared.usecase.crypt.ChangePasswordUseCase
import com.softartdev.notedelight.shared.usecase.crypt.CheckPasswordUseCase

class ChangeViewModel(
    private val checkPasswordUseCase: CheckPasswordUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase
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
            else -> when (checkPasswordUseCase(oldPassword)) {
                true -> {
                    changePasswordUseCase(oldPassword, newPassword)
                    ChangeResult.Success
                }
                false -> ChangeResult.IncorrectPasswordError
            }
        }
    }

    override fun errorResult(throwable: Throwable): ChangeResult =
        ChangeResult.Error(throwable.message)
}
