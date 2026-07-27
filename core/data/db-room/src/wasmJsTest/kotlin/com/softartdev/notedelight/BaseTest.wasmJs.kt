package com.softartdev.notedelight

import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.repository.WebSafeRepo

actual abstract class BaseTest actual constructor() {
    actual val safeRepo: SafeRepo = WebSafeRepo()

    actual fun deleteDb() = Unit
}
