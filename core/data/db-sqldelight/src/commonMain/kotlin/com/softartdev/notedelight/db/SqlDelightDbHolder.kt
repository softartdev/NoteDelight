package com.softartdev.notedelight.db

import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.async.coroutines.await
import app.cash.sqldelight.async.coroutines.awaitMigrate
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.shared.db.NoteQueries

interface SqlDelightDbHolder : DatabaseHolder {
    val logger: Logger
    val driver: SqlDriver
    val noteDb: NoteDb
    val noteQueries: NoteQueries

    suspend fun createSchema() {
        val targetVersion = NoteDb.Schema.version
        val currentVersion = driver.userVersion()
        when {
            currentVersion == 0L && driver.tableExists("note") -> {
                check(driver.hasCompatibleNoteTable()) {
                    "Database contains an incompatible note table"
                }
                driver.setUserVersion(targetVersion)
            }

            currentVersion == 0L -> {
                NoteDb.Schema.awaitCreate(driver)
                driver.setUserVersion(targetVersion)
            }

            currentVersion < targetVersion -> {
                NoteDb.Schema.awaitMigrate(driver, currentVersion, targetVersion)
                driver.setUserVersion(targetVersion)
            }

            currentVersion > targetVersion -> error(
                "Database version $currentVersion is newer than supported version $targetVersion",
            )
        }
    }
}

private suspend fun SqlDriver.userVersion(): Long = executeQuery(
    identifier = null,
    sql = "PRAGMA user_version",
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        check(cursor.next().value) { "PRAGMA user_version returned no row" }
        QueryResult.Value(requireNotNull(cursor.getLong(0)))
    },
).await()

private suspend fun SqlDriver.setUserVersion(version: Long) {
    execute(null, "PRAGMA user_version = $version", 0, null).await()
}

private suspend fun SqlDriver.tableExists(tableName: String): Boolean = executeQuery(
    identifier = null,
    sql = "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = ?",
    parameters = 1,
    binders = { bindString(0, tableName) },
    mapper = { cursor -> QueryResult.Value(cursor.next().value) },
).await()

private suspend fun SqlDriver.hasCompatibleNoteTable(): Boolean = executeQuery(
    identifier = null,
    sql = "PRAGMA table_info(note)",
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        val columns = buildSet {
            while (cursor.next().value) add(cursor.getString(1))
        }
        QueryResult.Value(columns == setOf("id", "title", "text", "dateCreated", "dateModified"))
    },
).await()
