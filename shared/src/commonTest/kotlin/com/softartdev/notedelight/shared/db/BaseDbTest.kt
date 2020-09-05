package com.softartdev.notedelight.shared.db

import kotlin.test.AfterTest
import kotlin.test.BeforeTest

open class BaseDbTest {
  @BeforeTest
  fun initDb() {
    createDriver()
  }

  @AfterTest
  fun closeDb() {
    closeDriver()
  }
}

/**
 * Init driver for each platform. Should *always* be called to setup test
 */
expect fun createDriver()

/**
 * Close driver for each platform. Should *always* be called to tear down test
 */
expect fun closeDriver()

/**
 * Platform specific access to HockeyDb
 */
expect fun BaseDbTest.getDb(): NoteDb
