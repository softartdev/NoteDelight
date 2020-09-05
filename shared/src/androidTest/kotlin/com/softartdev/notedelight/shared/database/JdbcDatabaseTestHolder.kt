package com.softartdev.notedelight.shared.database

import com.softartdev.notedelight.shared.db.NoteDb
import com.softartdev.notedelight.shared.db.TestSchema
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver

class JdbcDatabaseTestHolder: DatabaseHolder() {
    override val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    override val noteDb: NoteDb = TestSchema.getOrCreate(driver)
    override val noteQueries = noteDb.noteQueries

    init {
        TestSchema.create(driver)
    }

    override fun close() = driver.close()
}