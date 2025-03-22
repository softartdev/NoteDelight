package com.softartdev.notedelight

import com.softartdev.notedelight.db.NoteDb
import com.softartdev.notedelight.repository.SafeRepo

actual abstract class BaseTest actual constructor() {

    actual val safeRepo: SafeRepo = JvmTestSafeRepo()

    actual val noteDB: NoteDb
        get() {
            val jvmTestSafeRepo: JvmTestSafeRepo = safeRepo as JvmTestSafeRepo
            val dbHolder: JdbcDatabaseTestHolder = jvmTestSafeRepo.buildDbIfNeed()
            return dbHolder.noteDb
        }

    actual fun deleteDb() {}
}