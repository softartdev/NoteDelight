package com.softartdev.notedelight.shared.presentation.signin

import com.softartdev.notedelight.shared.base.BaseStateViewModel
import com.softartdev.notedelight.shared.usecase.crypt.CheckPasswordUseCase

class SignInViewModel(
    private val checkPasswordUseCase: CheckPasswordUseCase
) : BaseStateViewModel<SignInResult>() {

    override var initResult: SignInResult? = SignInResult.ShowSignInForm
    override val loadingResult: SignInResult = SignInResult.ShowProgress

    fun signIn(pass: CharSequence) = launch {
        if (pass.isNotEmpty()) {
            when (checkPasswordUseCase(pass)) {
                true -> SignInResult.NavMain
                false -> SignInResult.ShowIncorrectPassError
            }
        } else SignInResult.ShowEmptyPassError
    }

    override fun errorResult(throwable: Throwable): SignInResult = SignInResult.ShowError(throwable)
}
