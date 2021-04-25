package com.softartdev.notedelight.ui.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.shared.test.util.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val cryptUseCase = Mockito.mock(CryptUseCase::class.java)
    private val settingsViewModel = SettingsViewModel(cryptUseCase)

    @Test
    fun checkEncryptionTrue() = assertEncryption(true)

    @Test
    fun checkEncryptionFalse() = assertEncryption(false)

    private fun assertEncryption(encryption: Boolean) = runBlocking {
        Mockito.`when`(cryptUseCase.dbIsEncrypted()).thenReturn(encryption)
        settingsViewModel.resultStateFlow.test {
            assertEquals(SecurityResult.Loading, expectItem())

            settingsViewModel.checkEncryption()
            assertEquals(SecurityResult.EncryptEnable(encryption), expectItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun changeEncryptionSetPasswordDialog() = runBlocking {
        settingsViewModel.resultStateFlow.test {
            assertEquals(SecurityResult.Loading, expectItem())

            settingsViewModel.changeEncryption(true)
            assertEquals(SecurityResult.SetPasswordDialog, expectItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun changeEncryptionPasswordDialog() = runBlocking {
        Mockito.`when`(cryptUseCase.dbIsEncrypted()).thenReturn(true)
        settingsViewModel.resultStateFlow.test {
            assertEquals(SecurityResult.Loading, expectItem())

            settingsViewModel.changeEncryption(false)
            assertEquals(SecurityResult.PasswordDialog, expectItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun changeEncryptionEncryptEnableFalse() = runBlocking {
        Mockito.`when`(cryptUseCase.dbIsEncrypted()).thenReturn(false)
        settingsViewModel.resultStateFlow.test {
            assertEquals(SecurityResult.Loading, expectItem())

            settingsViewModel.changeEncryption(false)
            assertEquals(SecurityResult.EncryptEnable(false), expectItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun changePasswordChangePasswordDialog() = runBlocking {
        Mockito.`when`(cryptUseCase.dbIsEncrypted()).thenReturn(true)
        settingsViewModel.resultStateFlow.test {
            assertEquals(SecurityResult.Loading, expectItem())

            settingsViewModel.changePassword()
            assertEquals(SecurityResult.ChangePasswordDialog, expectItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun changePasswordSetPasswordDialog() = runBlocking {
        Mockito.`when`(cryptUseCase.dbIsEncrypted()).thenReturn(false)
        settingsViewModel.resultStateFlow.test {
            assertEquals(SecurityResult.Loading, expectItem())

            settingsViewModel.changePassword()
            assertEquals(SecurityResult.SetPasswordDialog, expectItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun errorResult() {
        assertEquals(SecurityResult.Error("err"), settingsViewModel.errorResult(Throwable("err")))
    }
}