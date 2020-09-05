package com.softartdev.notedelight.shared.database

import com.softartdev.notedelight.shared.db.NoteDb
import com.softartdev.notedelight.shared.db.NoteQueries
import com.softartdev.notedelight.shared.db.createQueryWrapper
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

class IosDatabaseHolder : DatabaseHolder() {
    override val driver: SqlDriver = NativeSqliteDriver(NoteDb.Schema, DatabaseRepo.DB_NAME)
    override val noteDb: NoteDb = createQueryWrapper(driver)
    override val noteQueries: NoteQueries = noteDb.noteQueries

    override fun close() = driver.close()
}