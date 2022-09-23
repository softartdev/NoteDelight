package com.softartdev.notedelight.shared

import com.softartdev.notedelight.shared.database.DatabaseRepo
import com.softartdev.notedelight.shared.database.JdbcDbTestRepo

actual abstract class BaseTest {

    actual val dbRepo: DatabaseRepo = JdbcDbTestRepo()

    actual fun deleteDb() {
        TODO()
    }
}
