@file:OptIn(ExperimentalCoroutinesApi::class)

package com.softartdev.notedelight

import com.softartdev.notedelight.db.JdbcDatabaseHolder
import com.softartdev.notedelight.db.NoteDb
import com.softartdev.notedelight.repository.JvmSafeRepo
import com.softartdev.notedelight.repository.SafeRepo
import java.io.File
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher

actual abstract class BaseTest actual constructor() {

    private var _noteDb: NoteDb? = null
    private val testDbFile: File = File.createTempFile("notedelight-test-", ".db").apply {
        delete()
        deleteOnExit()
    }

    actual val safeRepo: SafeRepo = JvmSafeRepo(
        coroutineDispatchers = CoroutineDispatchersStub(testDispatcher = UnconfinedTestDispatcher())
    ).also { it.overrideDbPath(testDbFile.absolutePath) }

    actual suspend fun noteDB(): NoteDb {
        if (_noteDb == null) {
            val jvmTestSafeRepo: JvmSafeRepo = safeRepo as JvmSafeRepo
            val dbHolder: JdbcDatabaseHolder = jvmTestSafeRepo.buildDbIfNeed()
            _noteDb = dbHolder.noteDb
        }
        return _noteDb!!
    }

    actual fun deleteDb() {
        _noteDb = null
        testDbFile.delete()
    }
}
