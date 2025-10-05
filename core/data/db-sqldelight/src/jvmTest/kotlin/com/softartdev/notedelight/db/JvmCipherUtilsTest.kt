package com.softartdev.notedelight.db

import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.repository.SafeRepo.Companion.DB_NAME
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotEquals

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