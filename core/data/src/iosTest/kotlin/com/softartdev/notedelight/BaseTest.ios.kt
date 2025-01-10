package com.softartdev.notedelight

import com.softartdev.notedelight.db.IosCipherUtils
import com.softartdev.notedelight.db.IosDatabaseHolder
import com.softartdev.notedelight.db.NoteDb
import com.softartdev.notedelight.repository.IosSafeRepo
import com.softartdev.notedelight.repository.SafeRepo

actual abstract class BaseTest actual constructor() {

    actual val safeRepo: SafeRepo = IosSafeRepo()

    actual val noteDB: NoteDb
        get() {
            val jvmTestSafeRepo: IosSafeRepo = safeRepo as IosSafeRepo
            val dbHolder: IosDatabaseHolder = jvmTestSafeRepo.buildDbIfNeed()
            return dbHolder.noteDb
        }

    actual fun deleteDb() {
        IosCipherUtils.deleteDatabase()
    }
}