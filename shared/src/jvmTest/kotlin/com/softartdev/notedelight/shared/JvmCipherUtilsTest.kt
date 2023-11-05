package com.softartdev.notedelight.shared

import com.softartdev.notedelight.shared.db.SafeRepo.Companion.DB_NAME
import kotlin.test.*

class JvmCipherUtilsTest {

    @BeforeTest
    fun setUp() {
    }

    @AfterTest
    fun tearDown() {
    }

    @Test
    fun getDatabaseState() {
        val exp = PlatformSQLiteState.ENCRYPTED
        val act = JvmCipherUtils.getDatabaseState(DB_NAME)
        assertNotEquals(exp, act)
    }
}