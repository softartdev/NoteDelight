package com.softartdev.notedelight

import com.softartdev.notedelight.repository.SafeRepo

expect abstract class BaseTest() {

    val safeRepo: SafeRepo

    fun deleteDb()
}
