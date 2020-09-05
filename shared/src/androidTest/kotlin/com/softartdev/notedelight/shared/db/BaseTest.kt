package com.softartdev.notedelight.shared.db

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver

actual fun createDriver() {
  val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
  TestSchema.create(driver)
  Db.dbSetup(driver)
}

actual fun closeDriver() {
  Db.dbClear()
}

actual fun BaseDbTest.getDb(): NoteDb = Db.instance
