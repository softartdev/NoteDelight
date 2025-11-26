package com.softartdev.notedelight.db

import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.async.coroutines.awaitMigrate
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import co.touchlab.kermit.Logger
import java.sql.SQLException
import java.util.Properties

class JdbcDatabaseHolder(props: Properties = Properties()) : SqlDelightDbHolder {
    private val logger = Logger.withTag(this@JdbcDatabaseHolder::class.simpleName.toString())
    override val driver = JdbcSqliteDriver(
        url = JdbcSqliteDriver.IN_MEMORY + FilePathResolver().invoke(),// jdbc:sqlite:/.../notes.db
        properties = props
    )
    override val noteDb: NoteDb = createQueryWrapper(driver)
    override val noteQueries = noteDb.noteQueries

    private var currentVersion: Int
        get() {
            val queryResult = driver.execute(null, "PRAGMA user_version;", 0, null)
            val ver: Long = queryResult.value
            return ver.toInt()
        }
        set(value) {
            driver.execute(null, "PRAGMA user_version = $value;", 0, null)
        }

    override suspend fun createSchema() {
        if (currentVersion == 0) {
            try {
                NoteDb.Schema.awaitCreate(driver)
            } catch (sqlException: SQLException) {
                logger.e { sqlException.localizedMessage }
            } catch (t: Throwable) {
                logger.e(t) { "Error creating database schema" }
            }
            currentVersion = 1
        } else if (NoteDb.Schema.version > currentVersion) {
            NoteDb.Schema.awaitMigrate(driver, currentVersion.toLong(), NoteDb.Schema.version)
        }
    }

    override fun close() = driver.close()
}