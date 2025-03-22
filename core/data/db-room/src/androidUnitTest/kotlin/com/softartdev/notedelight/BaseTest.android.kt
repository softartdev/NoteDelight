package com.softartdev.notedelight

import com.softartdev.notedelight.repository.SafeRepo

actual abstract class BaseTest actual constructor() {

    actual val safeRepo: SafeRepo = JvmTestSafeRepo()

    actual fun deleteDb() {}
}