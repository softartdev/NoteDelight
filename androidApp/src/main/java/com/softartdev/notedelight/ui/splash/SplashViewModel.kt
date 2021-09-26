package com.softartdev.notedelight.ui.splash

import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.shared.base.BaseViewModel

class SplashViewModel(
        private val cryptUseCase: CryptUseCase
) : BaseViewModel<SplashResult>() {

    override val loadingResult: SplashResult = SplashResult.Loading

    fun checkEncryption() = launch {
        when (cryptUseCase.dbIsEncrypted()) {
            true -> SplashResult.NavSignIn
            false -> SplashResult.NavMain
        }
    }

    override fun errorResult(throwable: Throwable) = SplashResult.ShowError(throwable.message)
}
