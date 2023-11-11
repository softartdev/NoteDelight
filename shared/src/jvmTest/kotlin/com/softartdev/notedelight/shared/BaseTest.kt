package com.softartdev.notedelight.shared

import com.softartdev.notedelight.shared.db.JvmSafeRepo
import com.softartdev.notedelight.shared.db.SafeRepo

actual abstract class BaseTest actual constructor() {
    actual val safeRepo: SafeRepo = JvmSafeRepo()

    actual fun deleteDb() {
    }
}