package com.softartdev.notedelight.db

import com.softartdev.notedelight.BaseTest
import com.softartdev.notedelight.model.PlatformSQLiteState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import platform.Foundation.NSFileManager
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.usecase.crypt.CheckPasswordUseCase

@ExperimentalCoroutinesApi
class IosCipherUtilsTest : BaseTest() {

    @Test
    @Ignore
    fun getDatabaseStateTest() {
        IosCipherUtils.deleteDatabase()
        var exp = PlatformSQLiteState.DOES_NOT_EXIST
        var act = IosCipherUtils.getDatabaseState(SafeRepo.DB_NAME)
        assertEquals(exp, act)

        IosDatabaseHolder().close()
        exp = PlatformSQLiteState.UNENCRYPTED
        act = IosCipherUtils.getDatabaseState(SafeRepo.DB_NAME)
        assertEquals(exp, act)

        IosCipherUtils.deleteDatabase()
        IosDatabaseHolder(key = "password").close()
        exp = PlatformSQLiteState.ENCRYPTED
        act = IosCipherUtils.getDatabaseState(SafeRepo.DB_NAME)
        assertEquals(exp, act)
    }

    @Test
    @Ignore
    fun checkKeyTest() = runTest {
        IosCipherUtils.deleteDatabase()
        IosDatabaseHolder().close()

        var exp = true
        var act = IosCipherUtils.checkKey(null, SafeRepo.DB_NAME)
        assertEquals(exp, act, "null pass")

        IosCipherUtils.deleteDatabase()
        IosDatabaseHolder(key = "password").close()

        exp = false
        act = IosCipherUtils.checkKey("incorrect password", SafeRepo.DB_NAME)
        assertEquals(exp, act, "incorrect pass")

        exp = true
        val checkPasswordUseCase = CheckPasswordUseCase(safeRepo)
        act = checkPasswordUseCase("password")
        assertEquals(exp, act, "correct pass")
    }

    @Test
    fun deleteDatabaseTest() {
        val dbPath = IosCipherUtils.getDatabasePath(SafeRepo.DB_NAME)
        val exp = NSFileManager.defaultManager.fileExistsAtPath(dbPath)
        val act = IosCipherUtils.deleteDatabase()
        assertEquals(exp, act)
    }
}