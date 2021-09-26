package com.softartdev.notedelight.ui.settings.security.enter

import android.text.Editable
import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.shared.base.BaseViewModel


class EnterViewModel (
        private val cryptUseCase: CryptUseCase
) : BaseViewModel<EnterResult>() {

    override var initResult: EnterResult? = EnterResult.InitState
    override val loadingResult: EnterResult = EnterResult.Loading

    fun enterCheck(password: Editable) = launch {
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
