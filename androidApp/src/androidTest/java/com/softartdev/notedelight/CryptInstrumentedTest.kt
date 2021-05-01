package com.softartdev.notedelight

import android.text.SpannableStringBuilder
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.softartdev.notedelight.shared.database.DatabaseRepo
import com.softartdev.notedelight.shared.database.PlatformSQLiteState
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject

@MediumTest
@RunWith(AndroidJUnit4::class)
class CryptInstrumentedTest {

    private val dbRepo: DatabaseRepo by inject(DatabaseRepo::class.java)
    private val password = "password"

    @Test
    fun cryptTest() {
        assertEquals(PlatformSQLiteState.DOES_NOT_EXIST, dbRepo.databaseState)
        dbRepo.buildDatabaseInstanceIfNeed()
        assertEquals(PlatformSQLiteState.UNENCRYPTED, dbRepo.databaseState)
        dbRepo.encrypt(SpannableStringBuilder(password))
        assertEquals(PlatformSQLiteState.ENCRYPTED, dbRepo.databaseState)
        dbRepo.decrypt(SpannableStringBuilder(password))
        assertEquals(PlatformSQLiteState.UNENCRYPTED, dbRepo.databaseState)
    }
}