package com.softartdev.notedelight.shared.db

import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual fun createDriver() {
  Db.dbSetup(NativeSqliteDriver(NoteDb.Schema, "sampledb"))
  TestSchema.insertTestNotes(Db.instance.noteQueries)
}

actual fun closeDriver() {
  Db.dbClear()
}

actual fun BaseDbTest.getDb(): NoteDb = Db.instance
