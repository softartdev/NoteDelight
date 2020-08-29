package com.softartdev.notedelight.shared.db

import android.content.Context
import com.softartdev.notedelight.shared.data.SafeRepo.Companion.DB_NAME
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

  fun dbClear() {
    driverRef!!.close()
    dbRef = null
    driverRef = null
  }

  val instance: NoteDb
    get() = dbRef!!
}

fun Db.getInstance(driver: SqlDriver): NoteDb {
  if (!Db.ready) {
    Db.dbSetup(driver)
  }
  return Db.instance
}

fun Db.getInstance(context: Context): NoteDb {
  if (!Db.ready) {
    Db.dbSetup(AndroidSqliteDriver(NoteDb.Schema, context, DB_NAME))
  }
  return Db.instance
}

fun Db.getTestInstance(context: Context): NoteDb {
  if (!Db.ready) {
    Db.dbSetup(AndroidSqliteDriver(TestSchema, context, DB_NAME))
  }
  return Db.instance
}
