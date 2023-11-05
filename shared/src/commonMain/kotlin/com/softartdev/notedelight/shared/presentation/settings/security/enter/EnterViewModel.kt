package com.softartdev.notedelight.shared.presentation.settings.security.enter

import com.softartdev.notedelight.shared.base.BaseViewModel
import com.softartdev.notedelight.shared.usecase.crypt.ChangePasswordUseCase
import com.softartdev.notedelight.shared.usecase.crypt.CheckPasswordUseCase

class EnterViewModel (
    private val checkPasswordUseCase: CheckPasswordUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase
) : BaseViewModel<EnterResult>() {

    override var initResult: EnterResult? = EnterResult.InitState
    override val loadingResult: EnterResult = EnterResult.Loading

    fun enterCheck(password: CharSequence) = launch {
        if (password.isNotEmpty()) {
            when (checkPasswordUseCase(password)) {
                true -> {
                    changePasswordUseCase(password, null)
                    EnterResult.Success
                }
                false -> EnterResult.IncorrectPasswordError
            }
        } else EnterResult.EmptyPasswordError
    }

    override fun errorResult(throwable: Throwable) = EnterResult.Error(throwable.message)
}
