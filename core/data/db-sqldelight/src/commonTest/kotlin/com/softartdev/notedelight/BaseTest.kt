package com.softartdev.notedelight

import com.softartdev.notedelight.db.NoteDb
import com.softartdev.notedelight.repository.SafeRepo

expect abstract class BaseTest() {

    val safeRepo: SafeRepo

    suspend fun noteDB(): NoteDb

    fun deleteDb()
}
