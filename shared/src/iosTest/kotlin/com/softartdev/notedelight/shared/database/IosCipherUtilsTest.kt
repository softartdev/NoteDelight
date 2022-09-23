package com.softartdev.notedelight.shared.database

import com.softartdev.notedelight.shared.IosCipherUtils
import com.softartdev.notedelight.shared.PlatformSQLiteState
import com.softartdev.notedelight.shared.BaseTest
import com.softartdev.notedelight.shared.data.CryptUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import platform.Foundation.NSFileManager
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class IosCipherUtilsTest : BaseTest() {

    @Test
    @Ignore
    fun getDatabaseStateTest() {
        IosCipherUtils.deleteDatabase()
        var exp = PlatformSQLiteState.DOES_NOT_EXIST
        var act = IosCipherUtils.getDatabaseState(DatabaseRepo.DB_NAME)
        assertEquals(exp, act)

        IosDatabaseHolder().close()
        exp = PlatformSQLiteState.UNENCRYPTED
        act = IosCipherUtils.getDatabaseState(DatabaseRepo.DB_NAME)
        assertEquals(exp, act)

        IosCipherUtils.deleteDatabase()
        IosDatabaseHolder(key = "password").close()
        exp = PlatformSQLiteState.ENCRYPTED
        act = IosCipherUtils.getDatabaseState(DatabaseRepo.DB_NAME)
        assertEquals(exp, act)
    }

    @Test
    @Ignore
    fun checkKeyTest() = runTest {
        IosCipherUtils.deleteDatabase()
        IosDatabaseHolder().close()

        var exp = true
        var act = IosCipherUtils.checkKey(null, DatabaseRepo.DB_NAME)
        assertEquals(exp, act, "null pass")

        IosCipherUtils.deleteDatabase()
        IosDatabaseHolder(key = "password").close()

        exp = false
        act = IosCipherUtils.checkKey("incorrect password", DatabaseRepo.DB_NAME)
        assertEquals(exp, act, "incorrect pass")

        exp = true
//        act = IosCipherUtils.checkKey("password", DatabaseRepo.DB_NAME)
        act = CryptUseCase(dbRepo).checkPassword("password")
        assertEquals(exp, act, "correct pass")
    }

    @Test
    fun deleteDatabaseTest() {
        val dbPath = IosCipherUtils.getDatabasePath(DatabaseRepo.DB_NAME)
        val exp = NSFileManager.defaultManager.fileExistsAtPath(dbPath)
        val act = IosCipherUtils.deleteDatabase()
        assertEquals(exp, act)
    }
}