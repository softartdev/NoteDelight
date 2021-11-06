package com.softartdev.notedelight.shared

import com.softartdev.notedelight.shared.database.DatabaseRepo
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class JvmCipherUtilsTest {

    @BeforeTest
    fun setUp() {
    }

    @AfterTest
    fun tearDown() {
    }

    @Test
    fun getDatabaseState() {
        var exp = PlatformSQLiteState.UNENCRYPTED
        var act = JvmCipherUtils.getDatabaseState(DatabaseRepo.DB_NAME)
        assertEquals(exp, act)
    }
}