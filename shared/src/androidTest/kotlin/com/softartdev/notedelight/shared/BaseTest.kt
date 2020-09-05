package com.softartdev.notedelight.shared

import com.softartdev.notedelight.shared.database.DatabaseRepo
import com.softartdev.notedelight.shared.database.JdbcDbTestRepo
import com.softartdev.notedelight.shared.test.util.MainCoroutineRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.Rule

actual abstract class BaseTest {
    @get:Rule var coroutineTestRule = MainCoroutineRule()

    actual val dbRepo: DatabaseRepo = JdbcDbTestRepo()

    actual fun <T> runTest(block: suspend CoroutineScope.() -> T) {
        runBlocking { block() }
    }
}
