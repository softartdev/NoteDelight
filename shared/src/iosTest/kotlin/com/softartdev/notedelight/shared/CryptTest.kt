package com.softartdev.notedelight.shared

import com.softartdev.notedelight.shared.database.DatabaseRepo
import com.softartdev.notedelight.shared.database.DatabaseRepo.Companion.DB_NAME
import com.softartdev.notedelight.shared.database.IosDbRepo
import com.softartdev.notedelight.shared.database.TestSchema
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

        val dbRepo: DatabaseRepo = IosDbRepo()
        assertEquals(expected = PlatformSQLiteState.DOES_NOT_EXIST, actual = dbRepo.databaseState)

        TestSchema.insertTestNotes(dbRepo.noteQueries)
        assertEquals(expected = PlatformSQLiteState.UNENCRYPTED, actual = dbRepo.databaseState)

        var noteList = dbRepo.noteQueries.getAll().executeAsList()
        assertEquals(expected = 3, actual = noteList.size)

        val cipherVersion = IosCipherUtils.checkCipherVersion(dbName = DB_NAME)
        assertEquals(expected = "4.5.4 community", actual = cipherVersion)

        Napier.v("1️⃣'st step - encrypt database")
        dbRepo.closeDatabase()
        val password = "password"
        IosCipherUtils.encrypt(password, DB_NAME)
        assertEquals(expected = PlatformSQLiteState.ENCRYPTED, actual = dbRepo.databaseState)

        assertTrue(actual = IosCipherUtils.checkKey(password, DB_NAME))

        Napier.v("Password checked, try to check the data.")
        var dbHolder = dbRepo.buildDatabaseInstanceIfNeed(password)
        noteList = dbHolder.noteQueries.getAll().executeAsList()
        assertEquals(expected = 3, actual = noteList.size)

        Napier.v("2️⃣'nd step - change password")
        dbRepo.closeDatabase()
        val newPassword = "newPassword"
        dbRepo.rekey(password, newPassword)
        dbRepo.closeDatabase()
        assertTrue(actual = IosCipherUtils.checkKey(newPassword, DB_NAME))
        assertFalse(actual = IosCipherUtils.checkKey(password, DB_NAME))

        Napier.v("New password checked, try to check the data.")
        dbHolder = dbRepo.buildDatabaseInstanceIfNeed(newPassword)
        noteList = dbHolder.noteQueries.getAll().executeAsList()
        assertEquals(expected = 3, actual = noteList.size)

        Napier.v("3️⃣'rd step - decrypt database")
        dbRepo.closeDatabase()
        IosCipherUtils.decrypt(newPassword, DB_NAME)
        assertEquals(expected = PlatformSQLiteState.UNENCRYPTED, actual = dbRepo.databaseState)

        Napier.v("Password removed, try to check the data.")
        dbHolder = dbRepo.buildDatabaseInstanceIfNeed()
        noteList = dbHolder.noteQueries.getAll().executeAsList()
        assertEquals(expected = 3, actual = noteList.size)
    }
}