package com.softartdev.notedelight.shared

import com.softartdev.notedelight.shared.db.JvmTestSafeRepo
import com.softartdev.notedelight.shared.db.SafeRepo

actual abstract class BaseTest {

    actual val safeRepo: SafeRepo = JvmTestSafeRepo()

    actual fun deleteDb() {
        TODO()
    }
}
