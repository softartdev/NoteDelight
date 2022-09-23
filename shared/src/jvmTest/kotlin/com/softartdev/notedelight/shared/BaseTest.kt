package com.softartdev.notedelight.shared

import com.softartdev.notedelight.shared.database.DatabaseRepo
import com.softartdev.notedelight.shared.database.JdbcDbRepo

actual abstract class BaseTest actual constructor() {
    actual val dbRepo: DatabaseRepo = JdbcDbRepo()

    actual fun deleteDb() {
    }
}