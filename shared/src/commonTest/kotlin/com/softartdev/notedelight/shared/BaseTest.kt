package com.softartdev.notedelight.shared

import com.softartdev.notedelight.shared.database.DatabaseRepo
import kotlinx.coroutines.CoroutineScope

@Suppress("NO_ACTUAL_FOR_EXPECT")//TODO remove when fixed
expect abstract class BaseTest() {

    val dbRepo: DatabaseRepo

    fun <T> runTest(block: suspend CoroutineScope.() -> T)
}
