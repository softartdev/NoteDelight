package com.softartdev.notedelight.shared.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection
import co.touchlab.sqliter.DatabaseConfiguration

class IosDatabaseHolder(
    key: String? = null,
    rekey: String? = null,
    name: String = SafeRepo.DB_NAME,
    schema: SqlSchema<QueryResult.Value<Unit>> = NoteDb.Schema
) : DatabaseHolder() {
    private val configuration = createDatabaseConfiguration(name, schema, key, rekey)
    override val driver: SqlDriver = NativeSqliteDriver(configuration)
    override val noteDb: NoteDb = createQueryWrapper(driver)
    override val noteQueries: NoteQueries = noteDb.noteQueries

    override fun close() = driver.close()

    companion object {
        fun createDatabaseConfiguration(
            name: String = SafeRepo.DB_NAME,
            schema: SqlSchema<QueryResult.Value<Unit>> = NoteDb.Schema,
            key: String? = null,
            rekey: String? = null
        ): DatabaseConfiguration = DatabaseConfiguration(
            name = name,
            version = schema.version.toInt(),
            create = { connection ->
                wrapConnection(connection, schema::create)
            },
            upgrade = { connection, oldVersion, newVersion ->
                wrapConnection(connection) {
                    schema.migrate(it, oldVersion.toLong(), newVersion.toLong())
                }
            },
            encryptionConfig = DatabaseConfiguration.Encryption(key, rekey)
        )
    }
}