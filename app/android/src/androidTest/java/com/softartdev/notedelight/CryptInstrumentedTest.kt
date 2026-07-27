package com.softartdev.notedelight

import android.text.SpannableStringBuilder
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.repository.SafeRepo
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject

@MediumTest
@RunWith(AndroidJUnit4::class)
class CryptInstrumentedTest {
    private val logger = Logger.withTag(this@CryptInstrumentedTest::class.simpleName.toString())
    private val safeRepo: SafeRepo by inject(SafeRepo::class.java)
    private val password = "password"

    @Test
    fun cryptTest() = runTest {
        assertEquals(PlatformSQLiteState.DOES_NOT_EXIST, safeRepo.databaseState)
        safeRepo.buildDbIfNeed()
        val count: Long = safeRepo.noteDAO.count()
        logger.d { "notes count = $count" }
        assertEquals(PlatformSQLiteState.UNENCRYPTED, safeRepo.databaseState)
        safeRepo.encrypt(SpannableStringBuilder(password))
        assertEquals(PlatformSQLiteState.ENCRYPTED, safeRepo.databaseState)
        safeRepo.decrypt(SpannableStringBuilder(password))
        assertEquals(PlatformSQLiteState.UNENCRYPTED, safeRepo.databaseState)
    }

    @Test
    fun notePersistsAfterDatabaseReopen() = runTest {
        safeRepo.buildDbIfNeed()
        assertEquals("2", safeRepo.execute("PRAGMA user_version"))
        // SafeRoom must not be combined with a WAL mode that it does not manage.
        // Otherwise a cold process start can discard the previous process' WAL.
        assertNotEquals("wal", safeRepo.execute("PRAGMA journal_mode")?.lowercase())
        safeRepo.noteDAO.insert(
            Note(
                id = 0,
                title = "Persistent note",
                text = "Must survive closing and reopening the database",
                dateCreated = LocalDateTime(2026, 7, 21, 4, 27),
                dateModified = LocalDateTime(2026, 7, 21, 4, 27),
            )
        )
        assertEquals(1L, safeRepo.noteDAO.count())

        safeRepo.closeDatabase()
        safeRepo.buildDbIfNeed()

        assertEquals(1L, safeRepo.noteDAO.count())
    }
}
