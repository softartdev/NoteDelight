package com.softartdev.notedelight.shared.database

import com.softartdev.notedelight.shared.db.NoteDb
import com.softartdev.notedelight.shared.db.createQueryWrapper
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver

class JdbcDatabaseTestHolder: DatabaseHolder() {
    override val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    override val noteDb: NoteDb = createQueryWrapper(driver)
    override val noteQueries = noteDb.noteQueries

    override fun close() = driver.close()
}