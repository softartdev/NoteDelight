@file:OptIn(ExperimentalCoroutinesApi::class)

package com.softartdev.notedelight

import com.softartdev.notedelight.db.NoteDb
import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.repository.WebTestSafeRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher

actual abstract class BaseTest actual constructor() {

    private var _noteDb: NoteDb? = null

    actual val safeRepo: SafeRepo = WebTestSafeRepo(
        coroutineDispatchers = CoroutineDispatchersStub(testDispatcher = UnconfinedTestDispatcher())
    )

    actual suspend fun noteDB(): NoteDb {
        if (_noteDb == null) {
            val webTestSafeRepo: WebTestSafeRepo = safeRepo as WebTestSafeRepo
            val dbHolder = webTestSafeRepo.buildDbIfNeed()
            _noteDb = dbHolder.noteDb
        }
        return _noteDb!!
    }

    actual fun deleteDb() {}
}