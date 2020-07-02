package com.softartdev.notedelight.shared.db

import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual fun createDriver() {
  Db.dbSetup(NativeSqliteDriver(TestSchema, "sampledb"))
}

actual fun closeDriver() {
  Db.dbClear()
}

actual fun BaseTest.getDb(): NoteDb = Db.instance
