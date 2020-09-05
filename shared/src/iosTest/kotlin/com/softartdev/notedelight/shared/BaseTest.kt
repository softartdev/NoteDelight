package com.softartdev.notedelight.shared

import com.softartdev.notedelight.shared.database.DatabaseRepo
import com.softartdev.notedelight.shared.database.IosDbTestRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

actual abstract class BaseTest actual constructor() {

    actual val dbRepo: DatabaseRepo = IosDbTestRepo()

    actual fun <T> runTest(block: suspend CoroutineScope.() -> T) {
        runBlocking { block() }
    }
}