package com.softartdev.notedelight.shared

import com.softartdev.notedelight.shared.database.DatabaseRepo
import com.softartdev.notedelight.shared.database.IosDbRepo

actual abstract class BaseTest actual constructor() {

    actual val dbRepo: DatabaseRepo = IosDbRepo()

    actual fun deleteDb() {
        IosCipherUtils.deleteDatabase()
    }
}