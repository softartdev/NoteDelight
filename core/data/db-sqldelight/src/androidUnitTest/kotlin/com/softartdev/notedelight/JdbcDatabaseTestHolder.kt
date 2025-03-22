package com.softartdev.notedelight

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.softartdev.notedelight.db.NoteDb
import com.softartdev.notedelight.db.SqlDelightDbHolder
import com.softartdev.notedelight.db.createQueryWrapper

class JdbcDatabaseTestHolder: SqlDelightDbHolder {
    override val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    override val noteDb: NoteDb = createQueryWrapper(driver)
    override val noteQueries = noteDb.noteQueries

    init {
        NoteDb.Schema.create(driver)
    }

    override fun close() = driver.close()
}