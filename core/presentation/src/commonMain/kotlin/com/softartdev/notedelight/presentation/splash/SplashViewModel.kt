package com.softartdev.notedelight.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.navigation.AppNavGraph
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.repository.SafeRepo
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val safeRepo: SafeRepo,
    private val router: Router
) : ViewModel() {
    private val mutableStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val stateFlow: StateFlow<Boolean> = mutableStateFlow

    fun checkEncryption() = viewModelScope.launch {
        try {
            router.navigateClearingBackStack(
                route = when (safeRepo.databaseState) {
                    PlatformSQLiteState.ENCRYPTED -> AppNavGraph.SignIn
                    else -> AppNavGraph.Main
                }
            )
        } catch (error: Throwable) {
            Napier.e("❌", error)
            router.navigate(route = AppNavGraph.ErrorDialog(message = error.message))
        }
        mutableStateFlow.value = false
    }
}
