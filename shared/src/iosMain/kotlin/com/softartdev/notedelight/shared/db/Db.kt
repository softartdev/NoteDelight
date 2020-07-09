package com.softartdev.notedelight.shared.db

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.freeze

object Db {
  private val driverRef = AtomicReference<SqlDriver?>(null)
  private val dbRef = AtomicReference<NoteDb?>(null)

  fun dbSetup(driver: SqlDriver) {
    val db = createQueryWrapper(driver)
    driverRef.value = driver.freeze()
    dbRef.value = db.freeze()
  }

  fun dbClear() {
    driverRef.value?.close()
    dbRef.value = null
    driverRef.value = null
  }

  // Called from Swift
  @Suppress("unused")
  fun defaultDriver() {
    Db.dbSetup(NativeSqliteDriver(NoteDb.Schema, "sampledb"))
  }

  fun defaultTestDriver() {
    Db.dbSetup(NativeSqliteDriver(TestSchema, "sampledb"))
  }

  val instance: NoteDb
    get() = dbRef.value!!
}
