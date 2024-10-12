package com.softartdev.notedelight.shared.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softartdev.notedelight.shared.PlatformSQLiteState
import com.softartdev.notedelight.shared.db.SafeRepo
import com.softartdev.notedelight.shared.navigation.AppNavGraph
import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.usecase.crypt.CheckSqlCipherVersionUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val safeRepo: SafeRepo,
    private val checkSqlCipherVersionUseCase: CheckSqlCipherVersionUseCase,
    private val router: Router
) : ViewModel() {
    private val mutableStateFlow: MutableStateFlow<SecurityResult> = MutableStateFlow(
        value = SecurityResult(
            navBack = router::popBackStack,
            changeTheme = this@SettingsViewModel::changeTheme,
            checkEncryption = this@SettingsViewModel::checkEncryption,
            changeEncryption = this@SettingsViewModel::changeEncryption,
            changePassword = this@SettingsViewModel::changePassword,
            showCipherVersion = this@SettingsViewModel::showCipherVersion,
            disposeOneTimeEvents = this@SettingsViewModel::disposeOneTimeEvents
        )
    )
    val stateFlow: StateFlow<SecurityResult> = mutableStateFlow

    private val dbIsEncrypted: Boolean
        get() = safeRepo.databaseState == PlatformSQLiteState.ENCRYPTED

    private fun changeTheme() = router.navigate(route = AppNavGraph.ThemeDialog.name)

    private fun checkEncryption() = viewModelScope.launch {
        mutableStateFlow.update(SecurityResult::showLoading)
        try {
            mutableStateFlow.update { result -> result.copy(encryption = dbIsEncrypted) }
        } catch (e: Throwable) {
            Napier.e("❌", e)
            router.navigate(route = AppNavGraph.ErrorDialog.argRoute(message = e.message))
        } finally {
            mutableStateFlow.update(SecurityResult::hideLoading)
        }
    }

    private fun changeEncryption(checked: Boolean) = viewModelScope.launch {
        mutableStateFlow.update(SecurityResult::showLoading)
        try {
            when {
                checked -> router.navigate(route = AppNavGraph.ConfirmPasswordDialog.name)
                else -> when {
                    dbIsEncrypted -> router.navigate(route = AppNavGraph.EnterPasswordDialog.name)
                    else -> mutableStateFlow.update(SecurityResult::hideEncryption)
                }
            }
        } catch (e: Throwable) {
            Napier.e("❌", e)
            router.navigate(route = AppNavGraph.ErrorDialog.argRoute(message = e.message))
        } finally {
            mutableStateFlow.update(SecurityResult::hideLoading)
        }
    }

    private fun changePassword() = viewModelScope.launch {
        mutableStateFlow.update(SecurityResult::showLoading)
        try {
            when {
                dbIsEncrypted -> router.navigate(route = AppNavGraph.ChangePasswordDialog.name)
                else -> router.navigate(route = AppNavGraph.ConfirmPasswordDialog.name)
            }
        } catch (e: Throwable) {
            Napier.e("❌", e)
            router.navigate(route = AppNavGraph.ErrorDialog.argRoute(message = e.message))
        } finally {
            mutableStateFlow.update(SecurityResult::hideLoading)
        }
    }

    private fun showCipherVersion() = viewModelScope.launch {
        mutableStateFlow.update(SecurityResult::showLoading)
        try {
            val cipherVersion: String? = checkSqlCipherVersionUseCase.invoke()
            mutableStateFlow.update { result -> result.copy(snackBarMessage = cipherVersion) }
        } catch (e: Throwable) {
            Napier.e("❌", e)
            router.navigate(route = AppNavGraph.ErrorDialog.argRoute(message = e.message))
        } finally {
            mutableStateFlow.update(SecurityResult::hideLoading)
        }
    }

    private fun disposeOneTimeEvents() = viewModelScope.launch {
        mutableStateFlow.update(SecurityResult::hideSnackBarMessage)
    }
}
