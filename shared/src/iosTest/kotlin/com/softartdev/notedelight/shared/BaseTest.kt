package com.softartdev.notedelight.shared

import com.softartdev.cipherdelight.IosCipherUtils
import com.softartdev.notedelight.shared.database.DatabaseRepo
import com.softartdev.notedelight.shared.database.IosDbRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

actual abstract class BaseTest actual constructor() {

    actual val dbRepo: DatabaseRepo = IosDbRepo()

    actual fun <T> runTest(block: suspend CoroutineScope.() -> T) {
        runBlocking { block() }
    }

    actual fun deleteDb() {
        IosCipherUtils.deleteDatabase()
    }
}