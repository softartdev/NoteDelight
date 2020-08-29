package com.softartdev.notedelight

import android.text.SpannableStringBuilder
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.commonsware.cwac.saferoom.SQLCipherUtils
import com.softartdev.notedelight.shared.data.SafeRepo
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject

@MediumTest
@RunWith(AndroidJUnit4::class)
class CryptInstrumentedTest {

    private val safeRepo by inject(SafeRepo::class.java)
    private val password = "password"

    @Test
    fun cryptTest() {
        assertEquals(safeRepo.databaseState, SQLCipherUtils.State.UNENCRYPTED)
        safeRepo.encrypt(SpannableStringBuilder(password))
        assertEquals(safeRepo.databaseState, SQLCipherUtils.State.ENCRYPTED)
        safeRepo.decrypt(SpannableStringBuilder(password))
        assertEquals(safeRepo.databaseState, SQLCipherUtils.State.UNENCRYPTED)
    }
}