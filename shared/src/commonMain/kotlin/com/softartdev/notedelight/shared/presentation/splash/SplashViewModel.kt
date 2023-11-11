package com.softartdev.notedelight.shared.presentation.splash

import com.softartdev.notedelight.shared.PlatformSQLiteState
import com.softartdev.notedelight.shared.base.BaseViewModel
import com.softartdev.notedelight.shared.db.SafeRepo

class SplashViewModel(
    private val safeRepo: SafeRepo
) : BaseViewModel<SplashResult>() {

    override val loadingResult: SplashResult = SplashResult.Loading

    fun checkEncryption() = launch {
        when (safeRepo.databaseState) {
            PlatformSQLiteState.ENCRYPTED -> SplashResult.NavSignIn
            else -> SplashResult.NavMain
        }
    }

    override fun errorResult(throwable: Throwable) = SplashResult.ShowError(throwable.message)
}
