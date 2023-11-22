package com.softartdev.notedelight.shared

import com.softartdev.notedelight.shared.db.IosSafeRepo
import com.softartdev.notedelight.shared.db.SafeRepo

actual abstract class BaseTest actual constructor() {

    actual val safeRepo: SafeRepo = IosSafeRepo()

    actual fun deleteDb() {
        IosCipherUtils.deleteDatabase()
    }
}