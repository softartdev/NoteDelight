package com.softartdev.notedelight.shared

import com.softartdev.notedelight.shared.database.DatabaseRepo
import kotlinx.coroutines.CoroutineScope

expect abstract class BaseTest() {

    val dbRepo: DatabaseRepo

    fun <T> runTest(block: suspend CoroutineScope.() -> T)
}
