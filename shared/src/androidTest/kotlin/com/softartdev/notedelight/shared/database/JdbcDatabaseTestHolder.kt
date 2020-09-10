package com.softartdev.notedelight.shared.database

import com.softartdev.notedelight.shared.db.NoteDb
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver

class JdbcDatabaseTestHolder: DatabaseHolder() {
    override val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    override val noteDb: NoteDb = createQueryWrapper(driver)
    override val noteQueries = noteDb.noteQueries

    init {
        NoteDb.Schema.create(driver)
    }

    override fun close() = driver.close()
}