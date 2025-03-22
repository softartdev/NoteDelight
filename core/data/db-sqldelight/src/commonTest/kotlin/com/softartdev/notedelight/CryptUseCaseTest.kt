package com.softartdev.notedelight

import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.usecase.crypt.ChangePasswordUseCase
import com.softartdev.notedelight.usecase.crypt.CheckPasswordUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Ignore
@ExperimentalCoroutinesApi
class CryptUseCaseTest : BaseTest() {
    private val checkPasswordUseCase = CheckPasswordUseCase(safeRepo)
    private val changePasswordUseCase = ChangePasswordUseCase(safeRepo)
    
    private val dbIsEncrypted: Boolean
        get() = safeRepo.databaseState == PlatformSQLiteState.ENCRYPTED

    @BeforeTest
    fun setUp() = runTest {
        deleteDb()
    }

    @AfterTest
    fun tearDown() = runTest {
        safeRepo.closeDatabase()
    }

    @Test
    fun dbIsEncryptedTest() = runTest {
        assertFalse(dbIsEncrypted)
    }

    @Test
    fun checkPasswordTest() = runTest {
        assertTrue(checkPasswordUseCase(pass = ""), "empty pass")
        assertFalse(checkPasswordUseCase(pass = "~"), "not empty pass")
    }

    @Test
    fun changePasswordTest() = runTest {
        //prepare check
        assertFalse(dbIsEncrypted, "check db encrypt before")
        assertTrue(checkPasswordUseCase(pass = ""), "check empty pass before")

        //encrypt
        val firstPass = "first password"
        changePasswordUseCase(null, firstPass)
        assertTrue(dbIsEncrypted, "check db encrypt after encrypt")
        assertTrue(checkPasswordUseCase(pass = firstPass), "check correct pass after encrypt")
        assertFalse(checkPasswordUseCase(pass = ""), "check empty pass after encrypt")

        //rekey
        val secondPass = "second password"
        changePasswordUseCase(firstPass, secondPass)
        assertTrue(dbIsEncrypted, "check db encrypt after rekey")
        assertTrue(checkPasswordUseCase(pass = secondPass), "check correct pass after rekey")
        assertFalse(checkPasswordUseCase(pass = firstPass), "check incorrect pass after rekey")

        //decrypt
        changePasswordUseCase(secondPass, null)
        assertFalse(dbIsEncrypted, "check db encrypt after decrypt")
        assertTrue(checkPasswordUseCase(pass = ""), "check empty pass after decrypt")
        assertFalse(checkPasswordUseCase(pass = firstPass), "check first pass after decrypt")
        assertFalse(checkPasswordUseCase(pass = secondPass), "check second pass after decrypt")
    }
}