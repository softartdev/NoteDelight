package com.softartdev.notedelight.shared.presentation.settings.security.confirm

import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.shared.base.BaseViewModel


class ConfirmViewModel (
        private val cryptUseCase: CryptUseCase
) : BaseViewModel<ConfirmResult>() {

    override var initResult: ConfirmResult? = ConfirmResult.InitState
    override val loadingResult: ConfirmResult = ConfirmResult.Loading

    fun conformCheck(password: CharSequence, repeatPassword: CharSequence) = launch {
        when {
            password.toString() != repeatPassword.toString() -> ConfirmResult.PasswordsNoMatchError
            password.isEmpty() -> ConfirmResult.EmptyPasswordError
            else -> {
                cryptUseCase.changePassword(null, password)
                ConfirmResult.Success
            }
        }
    }

    override fun errorResult(throwable: Throwable): ConfirmResult = ConfirmResult.Error(throwable.message)
}
