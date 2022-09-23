package com.softartdev.notedelight.shared

import com.softartdev.notedelight.shared.database.DatabaseRepo

expect abstract class BaseTest() {

    val dbRepo: DatabaseRepo

    fun deleteDb()
}
