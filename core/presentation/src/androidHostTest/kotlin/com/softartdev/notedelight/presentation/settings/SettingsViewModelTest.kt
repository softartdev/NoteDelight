package com.softartdev.notedelight.presentation.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.interactor.LocaleInteractor
import com.softartdev.notedelight.interactor.SnackbarInteractor
import com.softartdev.notedelight.interactor.SnackbarMessage
import com.softartdev.notedelight.model.LanguageEnum
import com.softartdev.notedelight.model.PlatformSQLiteState.DOES_NOT_EXIST
import com.softartdev.notedelight.model.PlatformSQLiteState.ENCRYPTED
import com.softartdev.notedelight.model.PlatformSQLiteState.UNENCRYPTED
import com.softartdev.notedelight.navigation.AppNavGraph
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.presentation.MainDispatcherRule
import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.usecase.crypt.CheckSqlCipherVersionUseCase
import com.softartdev.notedelight.usecase.settings.RevealFileListUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class SettingsViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockSafeRepo = Mockito.mock(SafeRepo::class.java)
    private val checkSqlCipherVersionUseCase = CheckSqlCipherVersionUseCase(mockSafeRepo)
    private val mockRouter = Mockito.mock(Router::class.java)
    private val mockSnackbarInteractor = Mockito.mock(SnackbarInteractor::class.java)
    private val mockLocaleInteractor = Mockito.mock(LocaleInteractor::class.java)
    private val settingsViewModel = SettingsViewModel(mockSafeRepo, checkSqlCipherVersionUseCase, mockSnackbarInteractor, mockRouter, RevealFileListUseCase(), mockLocaleInteractor)

    @After
    fun tearDown() = runTest {
        Mockito.reset(mockSafeRepo, mockSnackbarInteractor, mockRouter)
    }

    @Test
    fun changeTheme() = runTest {
        settingsViewModel.onAction(SettingsAction.ChangeTheme)
        Mockito.verify(mockRouter).navigate(route = AppNavGraph.ThemeDialog)
        Mockito.verifyNoMoreInteractions(mockRouter)
    }

    @Test
    fun checkEncryptionTrue() = assertEncryption(true)

    @Test
    fun checkEncryptionFalse() = assertEncryption(false)

    private fun assertEncryption(encryption: Boolean) = runTest {
        val platformSQLiteState = if (encryption) ENCRYPTED else UNENCRYPTED
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(platformSQLiteState)
        Mockito.`when`(mockLocaleInteractor.languageEnum).thenReturn(LanguageEnum.ENGLISH)
        settingsViewModel.stateFlow.test {
            assertFalse(awaitItem().loading)
            settingsViewModel.updateSwitches()
            if (encryption) awaitItem().let { result: SecurityResult ->
                assertFalse(result.loading)
                assertTrue(result.encryption)
            }
            expectNoEvents()
        }
        Mockito.verifyNoMoreInteractions(mockRouter)
    }

    @Test
    fun changeEncryptionSetPasswordDialog() = runTest {
        settingsViewModel.stateFlow.test {
            assertFalse(awaitItem().loading)
            settingsViewModel.onAction(SettingsAction.ChangeEncryption(true))
            Mockito.verify(mockRouter).navigate(route = AppNavGraph.ConfirmPasswordDialog)
            expectNoEvents()
        }
        Mockito.verifyNoMoreInteractions(mockRouter)
    }

    @Test
    fun changeEncryptionPasswordDialog() = runTest {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(ENCRYPTED)
        settingsViewModel.stateFlow.test {
            assertFalse(awaitItem().loading)
            settingsViewModel.onAction(SettingsAction.ChangeEncryption(false))
            Mockito.verify(mockRouter).navigate(route = AppNavGraph.EnterPasswordDialog)
            expectNoEvents()
        }
        Mockito.verifyNoMoreInteractions(mockRouter)
    }

    @Test
    fun changeEncryptionEncryptEnableFalse() = runTest {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(UNENCRYPTED)
        settingsViewModel.stateFlow.test {
            assertFalse(awaitItem().loading)
            settingsViewModel.onAction(SettingsAction.ChangeEncryption(false))

            Mockito.verifyNoMoreInteractions(mockRouter)
            expectNoEvents()
        }
        Mockito.verifyNoMoreInteractions(mockRouter)
    }

    @Test
    fun changePasswordChangePasswordDialog() = runTest {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(ENCRYPTED)
        settingsViewModel.stateFlow.test {
            assertFalse(awaitItem().loading)
            settingsViewModel.onAction(SettingsAction.ChangePassword)
            Mockito.verify(mockRouter).navigate(route = AppNavGraph.ChangePasswordDialog)
            expectNoEvents()
        }
        Mockito.verifyNoMoreInteractions(mockRouter)
    }

    @Test
    fun changePasswordSetPasswordDialog() = runTest {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(DOES_NOT_EXIST)
        settingsViewModel.stateFlow.test {
            assertFalse(awaitItem().loading)
            settingsViewModel.onAction(SettingsAction.ChangePassword)
            Mockito.verify(mockRouter).navigate(route = AppNavGraph.ConfirmPasswordDialog)
            expectNoEvents()
        }
        Mockito.verifyNoMoreInteractions(mockRouter)
    }

    @Test
    fun showCipherVersion() = runTest {
        val cipherVersion = "4.5.0"
        Mockito.`when`(mockSafeRepo.execute("PRAGMA cipher_version;")).thenReturn(cipherVersion)
        settingsViewModel.onAction(SettingsAction.ShowCipherVersion)
        Mockito.verify(mockSnackbarInteractor).showMessage(SnackbarMessage.Copyable(cipherVersion))
        Mockito.verifyNoMoreInteractions(mockRouter)
    }

    @Test
    fun showDatabasePath() = runTest {
        val dbPath = "/data/data/com.softartdev.notedelight/databases/note.db"
        Mockito.`when`(mockSafeRepo.dbPath).thenReturn(dbPath)
        settingsViewModel.onAction(SettingsAction.ShowDatabasePath)
        Mockito.verify(mockSnackbarInteractor).showMessage(SnackbarMessage.Copyable(dbPath))
        Mockito.verifyNoMoreInteractions(mockRouter)
    }

    @Test
    fun fileListHiddenByDefault() = runTest {
        settingsViewModel.stateFlow.test {
            assertFalse(awaitItem().fileListVisible)
        }
    }

    @Test
    fun revealFileListAfterRapidTaps() = runTest {
        repeat(5) {
            settingsViewModel.onAction(SettingsAction.RevealFileList)
        }
        assertTrue(settingsViewModel.stateFlow.value.fileListVisible)
    }

    @Test
    fun slowTapsDoNotRevealFileList() = runTest {
        settingsViewModel.stateFlow.test {
            assertFalse(awaitItem().fileListVisible)
        }
        repeat(3) {
            settingsViewModel.onAction(SettingsAction.RevealFileList)
        }
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(2_000)
        repeat(2) {
            settingsViewModel.onAction(SettingsAction.RevealFileList)
        }
        assertFalse(settingsViewModel.stateFlow.value.fileListVisible)
    }
}
