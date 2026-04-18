package com.softartdev.notedelight

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.db.NoteDb
import com.softartdev.notedelight.db.SqlDelightDbHolder
import com.softartdev.notedelight.db.createQueryWrapper

class JdbcDatabaseTestHolder: SqlDelightDbHolder {
    override val logger = Logger.withTag("JdbcDatabaseTestHolder")
    override val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    override val noteDb: NoteDb = createQueryWrapper(driver)
    override val noteQueries = noteDb.noteQueries

    override fun close() = driver.close()
}