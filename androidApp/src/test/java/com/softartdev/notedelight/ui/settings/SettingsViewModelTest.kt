package com.softartdev.notedelight.ui.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.shared.test.util.MainCoroutineRule
import com.softartdev.notedelight.shared.test.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    private fun assertEncryption(encryption: Boolean) {
        Mockito.`when`(cryptUseCase.dbIsEncrypted()).thenReturn(encryption)
        settingsViewModel.checkEncryption()
        assertEquals(settingsViewModel.resultLiveData.getOrAwaitValue(), SecurityResult.EncryptEnable(encryption))
    }

    @Test
    fun changeEncryptionSetPasswordDialog() {
        settingsViewModel.changeEncryption(true)
        assertEquals(settingsViewModel.resultLiveData.getOrAwaitValue(), SecurityResult.SetPasswordDialog)
    }

    @Test
    fun changeEncryptionPasswordDialog() {
        Mockito.`when`(cryptUseCase.dbIsEncrypted()).thenReturn(true)
        settingsViewModel.changeEncryption(false)
        assertEquals(settingsViewModel.resultLiveData.getOrAwaitValue(), SecurityResult.PasswordDialog)
    }

    @Test
    fun changeEncryptionEncryptEnableFalse() {
        Mockito.`when`(cryptUseCase.dbIsEncrypted()).thenReturn(false)
        settingsViewModel.changeEncryption(false)
        assertEquals(settingsViewModel.resultLiveData.getOrAwaitValue(), SecurityResult.EncryptEnable(false))
    }

    @Test
    fun changePasswordChangePasswordDialog() {
        Mockito.`when`(cryptUseCase.dbIsEncrypted()).thenReturn(true)
        settingsViewModel.changePassword()
        assertEquals(settingsViewModel.resultLiveData.getOrAwaitValue(), SecurityResult.ChangePasswordDialog)
    }

    @Test
    fun changePasswordSetPasswordDialog() {
        Mockito.`when`(cryptUseCase.dbIsEncrypted()).thenReturn(false)
        settingsViewModel.changePassword()
        assertEquals(settingsViewModel.resultLiveData.getOrAwaitValue(), SecurityResult.SetPasswordDialog)
    }

    @Test
    fun errorResult() {
        assertEquals(SecurityResult.Error("err"), settingsViewModel.errorResult(Throwable("err")))
    }
}