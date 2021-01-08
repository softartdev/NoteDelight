package com.softartdev.notedelight.shared.data

import com.softartdev.notedelight.shared.BaseTest
import kotlin.test.*

class CryptUseCaseTest : BaseTest() {

    private var cryptUseCase = CryptUseCase(dbRepo)

    @BeforeTest
    fun setUp() = runTest {
//        dbRepo.buildDatabaseInstanceIfNeed()
        deleteDb()
    }

    @AfterTest
    fun tearDown() = runTest {
        dbRepo.closeDatabase()
    }

    @Test
    fun dbIsEncryptedTest() = runTest {
        assertFalse(cryptUseCase.dbIsEncrypted())
    }

    @Test
    fun checkPasswordTest() = runTest {
        assertTrue(cryptUseCase.checkPassword(pass = ""), "empty pass")
        assertFalse(cryptUseCase.checkPassword(pass = "~"), "not empty pass")
    }

    @Test
    fun changePasswordTest() = runTest {
        //prepare check
        assertFalse(cryptUseCase.dbIsEncrypted(), "check db encrypt before")
        assertTrue(cryptUseCase.checkPassword(pass = ""), "check empty pass before")

        //encrypt
        val firstPass = "first password"
        cryptUseCase.changePassword(null, firstPass)
        assertTrue(cryptUseCase.dbIsEncrypted(), "check db encrypt after encrypt")
        assertTrue(cryptUseCase.checkPassword(pass = firstPass), "check correct pass after encrypt")
        assertFalse(cryptUseCase.checkPassword(pass = ""), "check empty pass after encrypt")

        //rekey
        val secondPass = "second password"
        cryptUseCase.changePassword(firstPass, secondPass)
        assertTrue(cryptUseCase.dbIsEncrypted(), "check db encrypt after rekey")
        assertTrue(cryptUseCase.checkPassword(pass = secondPass), "check correct pass after rekey")
        assertFalse(cryptUseCase.checkPassword(pass = firstPass), "check incorrect pass after rekey")

        //decrypt
        cryptUseCase.changePassword(secondPass, null)
        assertFalse(cryptUseCase.dbIsEncrypted(), "check db encrypt after decrypt")
        assertTrue(cryptUseCase.checkPassword(pass = ""), "check empty pass after decrypt")
        assertFalse(cryptUseCase.checkPassword(pass = firstPass), "check first pass after decrypt")
        assertFalse(cryptUseCase.checkPassword(pass = secondPass), "check second pass after decrypt")
    }
}