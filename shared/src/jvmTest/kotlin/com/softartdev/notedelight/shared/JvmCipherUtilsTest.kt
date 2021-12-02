package com.softartdev.notedelight.shared

import com.softartdev.notedelight.shared.database.DatabaseRepo
import kotlin.test.*

class JvmCipherUtilsTest {

    @BeforeTest
    fun setUp() {
    }

    @AfterTest
    fun tearDown() {
    }

    @Ignore("todo")
    @Test
    fun getDatabaseState() {
        val exp = PlatformSQLiteState.DOES_NOT_EXIST
        val act = JvmCipherUtils.getDatabaseState(DatabaseRepo.DB_NAME)
        assertEquals(exp, act)
    }
}