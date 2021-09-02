package com.softartdev.notedelight.shared.database

import com.softartdev.notedelight.shared.db.NoteDb
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver

class JdbcDatabaseHolder: DatabaseHolder() {
    override val driver = JdbcSqliteDriver(
        url = "jdbc:sqlite:${DatabaseRepo.DB_NAME}"
    )
    override val noteDb: NoteDb = createQueryWrapper(driver)
    override val noteQueries = noteDb.noteQueries

    override fun close() = driver.close()
}