package com.softartdev.notedelight

import com.softartdev.notedelight.db.IosCipherUtils
import com.softartdev.notedelight.db.TestSchema
import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.repository.IosSafeRepo
import com.softartdev.notedelight.repository.SafeRepo.Companion.DB_NAME
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import platform.Foundation.NSFileManager
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CryptTest {

    @BeforeTest
    fun setUp() {
        Napier.base(DebugAntilog())
        Napier.i("set-up")
    }

    @AfterTest
    fun tearDown() {
        Napier.i("tear-down")
        Napier.takeLogarithm()
    }

    @Test
    fun cryptTest() {
        Napier.v("0️⃣'th step - prepare database")
        val dbPath = IosCipherUtils.getDatabasePath(dbName = DB_NAME)
        Napier.d("dbPath: $dbPath")
        val dbExisted = NSFileManager.defaultManager.fileExistsAtPath(dbPath)
        val dbDeleted = IosCipherUtils.deleteDatabase()
        assertEquals(dbExisted, dbDeleted, message = "must be deleted if existed")

        val safeRepo = IosSafeRepo()
        assertEquals(expected = PlatformSQLiteState.DOES_NOT_EXIST, actual = safeRepo.databaseState)

        TestSchema.insertTestNotes(safeRepo.buildDbIfNeed().noteQueries)
        assertEquals(expected = PlatformSQLiteState.UNENCRYPTED, actual = safeRepo.databaseState)

        var noteList = safeRepo.buildDbIfNeed().noteQueries.getAll().executeAsList()
        assertEquals(expected = 3, actual = noteList.size)

        val cipherVersion = IosCipherUtils.checkCipherVersion(dbName = DB_NAME)
        assertEquals(expected = "4.5.4 community", actual = cipherVersion)

        Napier.v("1️⃣'st step - encrypt database")
        safeRepo.closeDatabase()
        val password = "password"
        IosCipherUtils.encrypt(password, DB_NAME)
        assertEquals(expected = PlatformSQLiteState.ENCRYPTED, actual = safeRepo.databaseState)

        assertTrue(actual = IosCipherUtils.checkKey(password, DB_NAME))

        Napier.v("Password checked, try to check the data.")
        var dbHolder = safeRepo.buildDbIfNeed(password)
        noteList = dbHolder.noteQueries.getAll().executeAsList()
        assertEquals(expected = 3, actual = noteList.size)

        Napier.v("2️⃣'nd step - change password")
        safeRepo.closeDatabase()
        val newPassword = "newPassword"
        safeRepo.rekey(password, newPassword)
        safeRepo.closeDatabase()
        assertTrue(actual = IosCipherUtils.checkKey(newPassword, DB_NAME))
        assertFalse(actual = IosCipherUtils.checkKey(password, DB_NAME))

        Napier.v("New password checked, try to check the data.")
        dbHolder = safeRepo.buildDbIfNeed(newPassword)
        noteList = dbHolder.noteQueries.getAll().executeAsList()
        assertEquals(expected = 3, actual = noteList.size)

        Napier.v("3️⃣'rd step - decrypt database")
        safeRepo.closeDatabase()
        IosCipherUtils.decrypt(newPassword, DB_NAME)
        assertEquals(expected = PlatformSQLiteState.UNENCRYPTED, actual = safeRepo.databaseState)

        Napier.v("Password removed, try to check the data.")
        dbHolder = safeRepo.buildDbIfNeed()
        noteList = dbHolder.noteQueries.getAll().executeAsList()
        assertEquals(expected = 3, actual = noteList.size)
    }
}