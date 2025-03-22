package com.softartdev.notedelight.db

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.aakira.napier.Napier
import java.sql.SQLException
import java.util.Properties

class JdbcDatabaseHolder(props: Properties = Properties()) : SqlDelightDbHolder {
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

    init {
        if (currentVersion == 0) {
            try {
                NoteDb.Schema.create(driver)
            } catch (sqlException: SQLException) {
                Napier.e(message = sqlException.localizedMessage)
            } catch (t: Throwable) {
                Napier.e(message = "Error creating database schema", throwable = t)
            }
            currentVersion = 1
        } else if (NoteDb.Schema.version > currentVersion) {
            NoteDb.Schema.migrate(driver, currentVersion.toLong(), NoteDb.Schema.version)
        }
    }

    override fun close() = driver.close()
}