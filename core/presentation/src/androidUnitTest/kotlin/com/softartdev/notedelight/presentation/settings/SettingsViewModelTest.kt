package com.softartdev.notedelight.presentation.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.model.PlatformSQLiteState.DOES_NOT_EXIST
import com.softartdev.notedelight.model.PlatformSQLiteState.ENCRYPTED
import com.softartdev.notedelight.model.PlatformSQLiteState.UNENCRYPTED
import com.softartdev.notedelight.navigation.AppNavGraph
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.presentation.MainDispatcherRule
import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.usecase.crypt.CheckSqlCipherVersionUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
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
    private val settingsViewModel = SettingsViewModel(mockSafeRepo, checkSqlCipherVersionUseCase, mockRouter)

    @Test
    fun changeTheme() = runTest {
        settingsViewModel.stateFlow.value.changeTheme.invoke()
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
        settingsViewModel.stateFlow.test {
            assertFalse(awaitItem().loading)
            settingsViewModel.stateFlow.value.checkEncryption.invoke()
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
            settingsViewModel.stateFlow.value.changeEncryption.invoke(true)
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
            settingsViewModel.stateFlow.value.changeEncryption.invoke(false)
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
            settingsViewModel.stateFlow.value.changeEncryption.invoke(false)

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
            settingsViewModel.stateFlow.value.changePassword.invoke()
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
            settingsViewModel.stateFlow.value.changePassword.invoke()
            Mockito.verify(mockRouter).navigate(route = AppNavGraph.ConfirmPasswordDialog)
            expectNoEvents()
        }
        Mockito.verifyNoMoreInteractions(mockRouter)
    }
}
