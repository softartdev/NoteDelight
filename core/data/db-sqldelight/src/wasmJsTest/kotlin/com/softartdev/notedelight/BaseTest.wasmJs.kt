@file:OptIn(ExperimentalCoroutinesApi::class)

package com.softartdev.notedelight

import com.softartdev.notedelight.db.NoteDb
import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.repository.WebSafeRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher

actual abstract class BaseTest actual constructor() {

    private var _noteDb: NoteDb? = null

    actual val safeRepo: SafeRepo = WebSafeRepo(
        coroutineDispatchers = CoroutineDispatchersStub(testDispatcher = UnconfinedTestDispatcher())
    )

    actual suspend fun noteDB(): NoteDb {
        if (_noteDb == null) {
            val webSafeRepo: WebSafeRepo = safeRepo as WebSafeRepo
            val dbHolder = webSafeRepo.buildDbIfNeed()
            _noteDb = dbHolder.noteDb
        }
        return _noteDb!!
    }

    actual fun deleteDb() {}
}