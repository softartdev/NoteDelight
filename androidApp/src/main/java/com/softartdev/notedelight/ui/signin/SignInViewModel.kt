package com.softartdev.notedelight.ui.signin

import android.text.Editable
import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.ui.base.BaseViewModel


class SignInViewModel (
        private val cryptUseCase: CryptUseCase
) : BaseViewModel<SignInResult>() {

    override val loadingResult: SignInResult = SignInResult.ShowProgress

    init {
        // workaround for change loading state by base class logic
        launch { SignInResult.ShowSignInForm }
    }

    fun signIn(pass: Editable) = launch {
        if (pass.isNotEmpty()) {
            when (cryptUseCase.checkPassword(pass)) {
                true -> SignInResult.NavMain
                false -> SignInResult.ShowIncorrectPassError
            }
        } else SignInResult.ShowEmptyPassError
    }

    override fun errorResult(throwable: Throwable): SignInResult = SignInResult.ShowError(throwable)
}
