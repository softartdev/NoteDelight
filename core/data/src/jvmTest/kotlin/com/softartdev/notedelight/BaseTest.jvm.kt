package com.softartdev.notedelight

import com.softartdev.notedelight.db.JdbcDatabaseHolder
import com.softartdev.notedelight.db.NoteDb
import com.softartdev.notedelight.repository.JvmSafeRepo
import com.softartdev.notedelight.repository.SafeRepo

actual abstract class BaseTest actual constructor() {

    actual val safeRepo: SafeRepo = JvmSafeRepo()

    actual val noteDB: NoteDb
        get() {
            val jvmTestSafeRepo: JvmSafeRepo = safeRepo as JvmSafeRepo
            val dbHolder: JdbcDatabaseHolder = jvmTestSafeRepo.buildDbIfNeed()
            return dbHolder.noteDb
        }

    actual fun deleteDb() {}
}