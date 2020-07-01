package com.softartdev.notedelight.shared.db

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

object Db {
  private var driverRef: SqlDriver? = null
  private var dbRef: NoteDb? = null

  val ready: Boolean
    get() = driverRef != null

  fun dbSetup(driver: SqlDriver) {
    val db = createQueryWrapper(driver)
    driverRef = driver
    dbRef = db
  }

  internal fun dbClear() {
    driverRef!!.close()
    dbRef = null
    driverRef = null
  }

  val instance: NoteDb
    get() = dbRef!!
}

fun Db.getInstance(context: Context): NoteDb {
  if (!Db.ready) {
    Db.dbSetup(AndroidSqliteDriver(NoteDb.Schema, context))
  }
  return Db.instance
}

fun Db.getTestInstance(context: Context): NoteDb {
  if (!Db.ready) {
    Db.dbSetup(AndroidSqliteDriver(TestSchema, context))
  }
  return Db.instance
}
