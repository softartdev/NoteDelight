package com.softartdev.notedelight.shared.db

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver

actual fun createDriver() {
  val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
  Db.dbSetup(driver)
  TestSchema.insertTestNotes(Db.instance.noteQueries)
}

actual fun closeDriver() {
  Db.dbClear()
}

actual fun BaseDbTest.getDb(): NoteDb = Db.instance
