package com.softartdev.notedelight.shared.database

import com.softartdev.notedelight.shared.db.NoteDb
import com.squareup.sqldelight.db.SqlCursor
import com.squareup.sqldelight.db.use
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver

class JdbcDatabaseHolder : DatabaseHolder() {
    override val driver = JdbcSqliteDriver(
        url = JdbcSqliteDriver.IN_MEMORY + DatabaseRepo.DB_NAME // jdbc:sqlite:notes.db
    )
    override val noteDb: NoteDb = createQueryWrapper(driver)
    override val noteQueries = noteDb.noteQueries

    private var currentVersion: Int
        get() {
            val sqlCursor: SqlCursor = driver.executeQuery(null, "PRAGMA user_version;", 0, null)
            val ver: Long? = sqlCursor.use { it.getLong(0) }
            return ver!!.toInt()
        }
        set(value) = driver.execute(null, "PRAGMA user_version = $value;", 0, null)

    init {
        if (currentVersion == 0) {
            NoteDb.Schema.create(driver)
            currentVersion = 1
        } else if (NoteDb.Schema.version > currentVersion) {
            NoteDb.Schema.migrate(driver, currentVersion, NoteDb.Schema.version)
        }
    }

    override fun close() = driver.close()
}