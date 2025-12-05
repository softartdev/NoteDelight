package com.softartdev.notedelight.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.interactor.LocaleInteractor
import com.softartdev.notedelight.interactor.SnackbarInteractor
import com.softartdev.notedelight.interactor.SnackbarMessage
import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.navigation.AppNavGraph
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.usecase.crypt.CheckSqlCipherVersionUseCase
import com.softartdev.notedelight.usecase.settings.RevealFileListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val safeRepo: SafeRepo,
    private val checkSqlCipherVersionUseCase: CheckSqlCipherVersionUseCase,
    private val snackbarInteractor: SnackbarInteractor,
    private val router: Router,
    private val revealFileListUseCase: RevealFileListUseCase,
    private val localeInteractor: LocaleInteractor,
) : ViewModel() {
    private val logger = Logger.withTag(this@SettingsViewModel::class.simpleName.toString())
    private val mutableStateFlow: MutableStateFlow<SecurityResult> = MutableStateFlow(
        value = SecurityResult()
    )
    val stateFlow: StateFlow<SecurityResult> = mutableStateFlow

    private val dbIsEncrypted: Boolean
        get() = safeRepo.databaseState == PlatformSQLiteState.ENCRYPTED

    private var cancelableThrowable: Throwable? = null // for skipping repeated error dialogs

    fun onAction(action: SettingsAction) = when (action) {
        is SettingsAction.NavBack -> navBack()
        is SettingsAction.ChangeTheme -> changeTheme()
        is SettingsAction.ChangeLanguage -> changeLanguage()
        is SettingsAction.CheckEncryption -> checkEncryption() //TODO use directly
        is SettingsAction.ChangeEncryption -> changeEncryption(action.checked)
        is SettingsAction.ChangePassword -> changePassword()
        is SettingsAction.ShowCipherVersion -> showCipherVersion()
        is SettingsAction.ShowDatabasePath -> showDatabasePath()
        is SettingsAction.ShowFileList -> showFileList()
        is SettingsAction.RevealFileList -> revealFileList()
    }

    private fun navBack() {
        if (!router.popBackStack()) router.navigate(route = AppNavGraph.Splash)
    }

    private fun changeTheme() = router.navigate(route = AppNavGraph.ThemeDialog)

    private fun changeLanguage() = router.navigate(route = AppNavGraph.LanguageDialog)

    private fun checkEncryption() = viewModelScope.launch {
        mutableStateFlow.update(SecurityResult::showLoading)
        try {
            mutableStateFlow.update { result ->
                result.copy(
                    encryption = dbIsEncrypted,
                    language = localeInteractor.languageEnum //TODO move to separate function
                )
            }
        } catch (e: Throwable) {
            handleError(e) { "error checking encryption" }
        } finally {
            mutableStateFlow.update(SecurityResult::hideLoading)
        }
    }

    private fun changeEncryption(checked: Boolean) = viewModelScope.launch {
        mutableStateFlow.update(SecurityResult::showLoading)
        try {
            when {
                checked -> router.navigate(route = AppNavGraph.ConfirmPasswordDialog)
                else -> when {
                    dbIsEncrypted -> router.navigate(route = AppNavGraph.EnterPasswordDialog)
                    else -> mutableStateFlow.update(SecurityResult::hideEncryption)
                }
            }
        } catch (e: Throwable) {
            handleError(e) { "error changing encryption" }
        } finally {
            mutableStateFlow.update(SecurityResult::hideLoading)
        }
    }

    private fun changePassword() = viewModelScope.launch {
        mutableStateFlow.update(SecurityResult::showLoading)
        try {
            when {
                dbIsEncrypted -> router.navigate(route = AppNavGraph.ChangePasswordDialog)
                else -> router.navigate(route = AppNavGraph.ConfirmPasswordDialog)
            }
        } catch (e: Throwable) {
            handleError(e) { "error changing password" }
        } finally {
            mutableStateFlow.update(SecurityResult::hideLoading)
        }
    }

    private fun showCipherVersion() = viewModelScope.launch {
        mutableStateFlow.update(SecurityResult::showLoading)
        try {
            val cipherVersion: String? = checkSqlCipherVersionUseCase.invoke()
            cipherVersion?.let { snackbarInteractor.showMessage(SnackbarMessage.Copyable(it)) }
        } catch (e: Throwable) {
            handleError(e) { "error checking sqlcipher version" }
        } finally {
            mutableStateFlow.update(SecurityResult::hideLoading)
        }
    }

    private fun showDatabasePath() = viewModelScope.launch {
        mutableStateFlow.update(SecurityResult::showLoading)
        try {
            val dbPath: String = safeRepo.dbPath
            snackbarInteractor.showMessage(SnackbarMessage.Copyable(dbPath))
        } catch (e: Throwable) {
            handleError(e) { "error getting database path" }
        } finally {
            mutableStateFlow.update(SecurityResult::hideLoading)
        }
    }

    private fun showFileList() = router.navigate(route = AppNavGraph.FileList)

    private fun revealFileList() {
        if (mutableStateFlow.value.fileListVisible) return
        revealFileListUseCase.onTap(viewModelScope) {
            mutableStateFlow.update(SecurityResult::showFileList)
        }
    }

    private inline fun handleError(e: Throwable, messageSupplier: () -> String) {
        logger.e(throwable = e, message = messageSupplier)
        if (sameErrors(cancelableThrowable, e)) return
        cancelableThrowable = e
        router.navigate(route = AppNavGraph.ErrorDialog(message = e.message))
    }

    private fun sameErrors(a: Throwable?, b: Throwable): Boolean {
        a ?: return false
        if (a::class != b::class) return false
        if (a.message != b.message) return false
        if (a.cause != null) {
            b.cause ?: return false
            if (a.cause!!::class != b.cause!!::class) return false
            if (a.cause!!.message != b.cause!!.message) return false
        }
        return true
    }
}
