package com.softartdev.notedelight

import android.text.SpannableStringBuilder
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.commonsware.cwac.saferoom.SQLCipherUtils
import com.softartdev.notedelight.shared.database.DatabaseRepo
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject

@MediumTest
@RunWith(AndroidJUnit4::class)
class CryptInstrumentedTest {

    private val dbRepo by inject(DatabaseRepo::class.java)
    private val password = "password"

    @Test
    fun cryptTest() {
        assertEquals(dbRepo.databaseState, SQLCipherUtils.State.UNENCRYPTED)
        dbRepo.encrypt(SpannableStringBuilder(password))
        assertEquals(dbRepo.databaseState, SQLCipherUtils.State.ENCRYPTED)
        dbRepo.decrypt(SpannableStringBuilder(password))
        assertEquals(dbRepo.databaseState, SQLCipherUtils.State.UNENCRYPTED)
    }
}