package com.softartdev.notedelight

import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.repository.AndroidSafeRepo
import com.softartdev.notedelight.usecase.crypt.ChangePasswordUseCase
import com.softartdev.notedelight.usecase.crypt.CheckPasswordUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class CryptUseCaseUnitTest {
    private val mockSafeRepo = Mockito.mock(AndroidSafeRepo::class.java)
    private val checkPasswordUseCase = CheckPasswordUseCase(mockSafeRepo)
    private val changePasswordUseCase = ChangePasswordUseCase(mockSafeRepo)

    @Before
    fun setUp() {
        Napier.base(PrintAntilog())
    }

    @After
    fun tearDown() = Napier.takeLogarithm()

    @Test
    fun `check correct password`() = runTest {
        val pass = StubEditable("correct password")
        assertTrue(checkPasswordUseCase(pass))
    }

    @Test
    fun `check incorrect password`() = runTest {
        val pass = StubEditable("incorrect password")
        Mockito.`when`(mockSafeRepo.buildDbIfNeed(anyObject())).thenThrow(RuntimeException())
        assertFalse(checkPasswordUseCase(pass))
    }

    @Test
    fun `change password for decrypt`() {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(PlatformSQLiteState.ENCRYPTED)
        val oldPass = StubEditable("old password")
        val newPass = null
        changePasswordUseCase(oldPass, newPass)
        Mockito.verify(mockSafeRepo).decrypt(oldPass)
    }

    @Test
    fun `change password for rekey`() {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(PlatformSQLiteState.ENCRYPTED)
        val oldPass = StubEditable("old password")
        val newPass = StubEditable("new password")
        changePasswordUseCase(oldPass, newPass)
        Mockito.verify(mockSafeRepo).rekey(oldPass, newPass)
    }

    @Test
    fun `change password for encrypt`() {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(PlatformSQLiteState.UNENCRYPTED)
        val oldPass = null
        val newPass = StubEditable("new password")
        changePasswordUseCase(oldPass, newPass)
        Mockito.verify(mockSafeRepo).encrypt(newPass)
    }
}
