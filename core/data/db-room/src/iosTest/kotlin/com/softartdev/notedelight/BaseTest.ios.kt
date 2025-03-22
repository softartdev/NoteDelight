package com.softartdev.notedelight

import com.softartdev.notedelight.db.IosCipherUtils
import com.softartdev.notedelight.repository.IosSafeRepo
import com.softartdev.notedelight.repository.SafeRepo

actual abstract class BaseTest actual constructor() {

    actual val safeRepo: SafeRepo = IosSafeRepo()

    actual fun deleteDb() {
        IosCipherUtils.deleteDatabase()
    }
}