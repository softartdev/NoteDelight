package com.softartdev.notedelight

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.usecase.crypt.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject

@MediumTest
@RunWith(AndroidJUnit4::class)
class CryptUseCaseInstrumentedTest {

    private val safeRepo: SafeRepo by inject(SafeRepo::class.java)
    private val changePasswordUseCase = ChangePasswordUseCase(safeRepo)
    private val checkPasswordUseCase = CheckPasswordUseCase(safeRepo)
    private val password = "password"

    private val dbIsEncrypted: Boolean
        get() = safeRepo.databaseState == PlatformSQLiteState.ENCRYPTED

    @Test
    fun cryptTest() = runBlocking {
        safeRepo.buildDbIfNeed()
        assertFalse(dbIsEncrypted)
        changePasswordUseCase(null, password)
        assertTrue(dbIsEncrypted)
        safeRepo.closeDatabase()
        assertFalse(checkPasswordUseCase("incorrect password"))
        assertTrue(checkPasswordUseCase(password))
    }

}