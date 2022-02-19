package com.softartdev.notedelight.shared.presentation.settings.security.enter

import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.shared.base.BaseViewModel


class EnterViewModel (
        private val cryptUseCase: CryptUseCase
) : BaseViewModel<EnterResult>() {

    override var initResult: EnterResult? = EnterResult.InitState
    override val loadingResult: EnterResult = EnterResult.Loading

    fun enterCheck(password: CharSequence) = launch {
        if (password.isNotEmpty()) {
            when (cryptUseCase.checkPassword(password)) {
                true -> {
                    cryptUseCase.changePassword(password, null)
                    EnterResult.Success
                }
                false -> EnterResult.IncorrectPasswordError
            }
        } else EnterResult.EmptyPasswordError
    }

    override fun errorResult(throwable: Throwable) = EnterResult.Error(throwable.message)
}
