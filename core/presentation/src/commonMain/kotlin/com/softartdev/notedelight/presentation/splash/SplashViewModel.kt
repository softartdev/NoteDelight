package com.softartdev.notedelight.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.navigation.AppNavGraph
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.util.CountingIdlingRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val safeRepo: SafeRepo,
    private val router: Router
) : ViewModel() {
    private val logger = Logger.withTag(this@SplashViewModel::class.simpleName.toString())
    private val mutableStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val stateFlow: StateFlow<Boolean> = mutableStateFlow

    fun checkEncryption() = viewModelScope.launch {
        CountingIdlingRes.increment()
        try {
            logger.d { "Building database if need..." }
            safeRepo.buildDbIfNeed()
            logger.d { "Database is ready, checking encryption state..." }
        } catch (error: Throwable) {
            logger.e(error) { "Error building database" }
        }
        try {
            router.navigateClearingBackStack(
                route = when (safeRepo.databaseState) {
                    PlatformSQLiteState.ENCRYPTED -> AppNavGraph.SignIn
                    else -> AppNavGraph.Main
                }
            )
        } catch (error: Throwable) {
            logger.e(error) { "❌" }
            router.navigate(route = AppNavGraph.ErrorDialog(message = error.message))
        } finally {
            mutableStateFlow.value = false
            CountingIdlingRes.decrement()
        }
    }
}
