package com.softartdev.notedelight

import com.softartdev.notedelight.db.IosCipherUtils
import com.softartdev.notedelight.db.IosDatabaseHolder
import com.softartdev.notedelight.db.NoteDb
import com.softartdev.notedelight.repository.IosSafeRepo
import com.softartdev.notedelight.repository.SafeRepo
import kotlinx.coroutines.test.UnconfinedTestDispatcher

actual abstract class BaseTest actual constructor() {

    private var _noteDb: NoteDb? = null

    actual val safeRepo: SafeRepo = IosSafeRepo(
        coroutineDispatchers = CoroutineDispatchersStub(testDispatcher = UnconfinedTestDispatcher())
    )

    actual suspend fun noteDB(): NoteDb {
        if (_noteDb == null) {
            val iosSafeRepo: IosSafeRepo = safeRepo as IosSafeRepo
            val dbHolder: IosDatabaseHolder = iosSafeRepo.buildDbIfNeed()
            _noteDb = dbHolder.noteDb
        }
        return _noteDb!!
    }

    actual fun deleteDb() {
        IosCipherUtils.deleteDatabase()
    }
}