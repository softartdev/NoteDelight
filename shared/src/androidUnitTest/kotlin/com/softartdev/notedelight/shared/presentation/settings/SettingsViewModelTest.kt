package com.softartdev.notedelight.shared.presentation.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.shared.PlatformSQLiteState.DOES_NOT_EXIST
import com.softartdev.notedelight.shared.PlatformSQLiteState.ENCRYPTED
import com.softartdev.notedelight.shared.PlatformSQLiteState.UNENCRYPTED
import com.softartdev.notedelight.shared.db.SafeRepo
import com.softartdev.notedelight.shared.presentation.MainDispatcherRule
import com.softartdev.notedelight.shared.usecase.crypt.CheckSqlCipherVersionUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class SettingsViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockSafeRepo = Mockito.mock(SafeRepo::class.java)
    private val checkSqlCipherVersionUseCase = CheckSqlCipherVersionUseCase(mockSafeRepo)
    private val settingsViewModel = SettingsViewModel(mockSafeRepo, checkSqlCipherVersionUseCase)

    @Test
    fun checkEncryptionTrue() = assertEncryption(true)

    @Test
    fun checkEncryptionFalse() = assertEncryption(false)

    private fun assertEncryption(encryption: Boolean) = runTest {
        val platformSQLiteState = if (encryption) ENCRYPTED else UNENCRYPTED
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(platformSQLiteState)
        settingsViewModel.resultStateFlow.test {
            assertEquals(SecurityResult.Loading, awaitItem())

            settingsViewModel.checkEncryption()
            assertEquals(SecurityResult.EncryptEnable(encryption), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun changeEncryptionSetPasswordDialog() = runTest {
        settingsViewModel.resultStateFlow.test {
            assertEquals(SecurityResult.Loading, awaitItem())

            settingsViewModel.changeEncryption(true)
            assertEquals(SecurityResult.SetPasswordDialog, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun changeEncryptionPasswordDialog() = runTest {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(ENCRYPTED)
        settingsViewModel.resultStateFlow.test {
            assertEquals(SecurityResult.Loading, awaitItem())

            settingsViewModel.changeEncryption(false)
            assertEquals(SecurityResult.PasswordDialog, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun changeEncryptionEncryptEnableFalse() = runTest {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(UNENCRYPTED)
        settingsViewModel.resultStateFlow.test {
            assertEquals(SecurityResult.Loading, awaitItem())

            settingsViewModel.changeEncryption(false)
            assertEquals(SecurityResult.EncryptEnable(false), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun changePasswordChangePasswordDialog() = runTest {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(ENCRYPTED)
        settingsViewModel.resultStateFlow.test {
            assertEquals(SecurityResult.Loading, awaitItem())

            settingsViewModel.changePassword()
            assertEquals(SecurityResult.ChangePasswordDialog, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun changePasswordSetPasswordDialog() = runTest {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(DOES_NOT_EXIST)
        settingsViewModel.resultStateFlow.test {
            assertEquals(SecurityResult.Loading, awaitItem())

            settingsViewModel.changePassword()
            assertEquals(SecurityResult.SetPasswordDialog, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun errorResult() {
        assertEquals(SecurityResult.Error("err"), settingsViewModel.errorResult(Throwable("err")))
    }
}