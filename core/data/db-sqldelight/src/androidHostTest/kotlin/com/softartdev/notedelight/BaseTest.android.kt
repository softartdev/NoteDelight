@file:OptIn(ExperimentalCoroutinesApi::class)

package com.softartdev.notedelight

import com.softartdev.notedelight.db.NoteDb
import com.softartdev.notedelight.repository.SafeRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher

actual abstract class BaseTest actual constructor() {

    private var _noteDb: NoteDb? = null

    actual val safeRepo: SafeRepo = JvmTestSafeRepo(
        coroutineDispatchers = CoroutineDispatchersStub(testDispatcher = UnconfinedTestDispatcher())
    )

    actual suspend fun noteDB(): NoteDb {
        if (_noteDb == null) {
            val jvmTestSafeRepo: JvmTestSafeRepo = safeRepo as JvmTestSafeRepo
            val dbHolder: JdbcDatabaseTestHolder = jvmTestSafeRepo.buildDbIfNeed()
            _noteDb = dbHolder.noteDb
        }
        return _noteDb!!
    }

    actual fun deleteDb() {}
}