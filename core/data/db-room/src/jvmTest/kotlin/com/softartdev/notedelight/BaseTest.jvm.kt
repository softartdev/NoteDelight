package com.softartdev.notedelight

import com.softartdev.notedelight.repository.JvmSafeRepo
import com.softartdev.notedelight.repository.SafeRepo

actual abstract class BaseTest actual constructor() {

    actual val safeRepo: SafeRepo = JvmSafeRepo()

    actual fun deleteDb() {}
}