@file:OptIn(ExperimentalCoroutinesApi::class)

package com.softartdev.notedelight

import co.touchlab.kermit.CommonWriter
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.db.IosCipherUtils
import com.softartdev.notedelight.db.TestSchema
import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.repository.IosSafeRepo
import com.softartdev.notedelight.repository.SafeRepo.Companion.DB_NAME
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import platform.Foundation.NSFileManager
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CryptTest {
    private val logger = Logger.withTag(this@CryptTest::class.simpleName.toString())

    @BeforeTest
    fun setUp() {
        Logger.setLogWriters(CommonWriter())
        logger.i { "set-up" }
    }

    @AfterTest
    fun tearDown() {
        logger.i { "tear-down" }
        Logger.setLogWriters()
    }

    @Test
    fun cryptTest() = runTest {
        logger.v { "0️⃣'th step - prepare database" }
        val dbPath = IosCipherUtils.getDatabasePath(dbName = DB_NAME)
        logger.d { "dbPath: $dbPath" }
        val dbExisted = NSFileManager.defaultManager.fileExistsAtPath(dbPath)
        val dbDeleted = IosCipherUtils.deleteDatabase()
        assertEquals(dbExisted, dbDeleted, message = "must be deleted if existed")

        val safeRepo = IosSafeRepo(
            coroutineDispatchers = CoroutineDispatchersStub(testDispatcher = UnconfinedTestDispatcher())
        )
        assertEquals(expected = PlatformSQLiteState.DOES_NOT_EXIST, actual = safeRepo.databaseState)

        TestSchema.insertTestNotes(safeRepo.buildDbIfNeed().noteQueries)
        assertEquals(expected = PlatformSQLiteState.UNENCRYPTED, actual = safeRepo.databaseState)

        var noteList = safeRepo.buildDbIfNeed().noteQueries.getAll().executeAsList()
        assertEquals(expected = 3, actual = noteList.size)

        val cipherVersion = IosCipherUtils.checkCipherVersion(dbName = DB_NAME)
        assertEquals(expected = "4.9.0 community", actual = cipherVersion)

        logger.v { "1️⃣'st step - encrypt database" }
        safeRepo.closeDatabase()
        val password = "password"
        IosCipherUtils.encrypt(password, DB_NAME)
        assertEquals(expected = PlatformSQLiteState.ENCRYPTED, actual = safeRepo.databaseState)

        assertTrue(actual = IosCipherUtils.checkKey(password, DB_NAME))

        logger.v { "Password checked, try to check the data." }
        var dbHolder = safeRepo.buildDbIfNeed(password)
        noteList = dbHolder.noteQueries.getAll().executeAsList()
        assertEquals(expected = 3, actual = noteList.size)

        logger.v { "2️⃣'nd step - change password" }
        safeRepo.closeDatabase()
        val newPassword = "newPassword"
        safeRepo.rekey(password, newPassword)
        safeRepo.closeDatabase()
        assertTrue(actual = IosCipherUtils.checkKey(newPassword, DB_NAME))
        assertFalse(actual = IosCipherUtils.checkKey(password, DB_NAME))

        logger.v { "New password checked, try to check the data." }
        dbHolder = safeRepo.buildDbIfNeed(newPassword)
        noteList = dbHolder.noteQueries.getAll().executeAsList()
        assertEquals(expected = 3, actual = noteList.size)

        logger.v { "3️⃣'rd step - decrypt database" }
        safeRepo.closeDatabase()
        IosCipherUtils.decrypt(newPassword, DB_NAME)
        assertEquals(expected = PlatformSQLiteState.UNENCRYPTED, actual = safeRepo.databaseState)

        logger.v { "Password removed, try to check the data." }
        dbHolder = safeRepo.buildDbIfNeed()
        noteList = dbHolder.noteQueries.getAll().executeAsList()
        assertEquals(expected = 3, actual = noteList.size)
    }
}