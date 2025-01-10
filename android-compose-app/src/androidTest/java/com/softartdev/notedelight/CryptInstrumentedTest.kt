package com.softartdev.notedelight

import android.text.SpannableStringBuilder
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.repository.SafeRepo
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject

@MediumTest
@RunWith(AndroidJUnit4::class)
class CryptInstrumentedTest {

    private val safeRepo: SafeRepo by inject(SafeRepo::class.java)
    private val password = "password"

    @Test
    fun cryptTest() {
        assertEquals(PlatformSQLiteState.DOES_NOT_EXIST, safeRepo.databaseState)
        safeRepo.buildDbIfNeed()
        assertEquals(PlatformSQLiteState.UNENCRYPTED, safeRepo.databaseState)
        safeRepo.encrypt(SpannableStringBuilder(password))
        assertEquals(PlatformSQLiteState.ENCRYPTED, safeRepo.databaseState)
        safeRepo.decrypt(SpannableStringBuilder(password))
        assertEquals(PlatformSQLiteState.UNENCRYPTED, safeRepo.databaseState)
    }
}