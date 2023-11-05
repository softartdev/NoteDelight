package com.softartdev.notedelight.shared

import com.softartdev.notedelight.shared.db.SafeRepo

expect abstract class BaseTest() {

    val safeRepo: SafeRepo

    fun deleteDb()
}
