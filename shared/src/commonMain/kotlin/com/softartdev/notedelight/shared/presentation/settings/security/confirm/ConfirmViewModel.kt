package com.softartdev.notedelight.shared.presentation.settings.security.confirm

import com.softartdev.notedelight.shared.base.BaseStateViewModel
import com.softartdev.notedelight.shared.usecase.crypt.ChangePasswordUseCase

class ConfirmViewModel (
    private val changePasswordUseCase: ChangePasswordUseCase
) : BaseStateViewModel<ConfirmResult>() {

    override var initResult: ConfirmResult? = ConfirmResult.InitState
    override val loadingResult: ConfirmResult = ConfirmResult.Loading

    fun conformCheck(password: CharSequence, repeatPassword: CharSequence) = launch {
        when {
            password.toString() != repeatPassword.toString() -> ConfirmResult.PasswordsNoMatchError
            password.isEmpty() -> ConfirmResult.EmptyPasswordError
            else -> {
                changePasswordUseCase(null, password)
                ConfirmResult.Success
            }
        }
    }

    override fun errorResult(throwable: Throwable): ConfirmResult = ConfirmResult.Error(throwable.message)
}
