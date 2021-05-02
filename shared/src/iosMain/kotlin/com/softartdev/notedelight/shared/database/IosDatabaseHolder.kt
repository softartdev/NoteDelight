package com.softartdev.notedelight.shared.database

import co.touchlab.sqliter.DatabaseConfiguration
import com.softartdev.notedelight.shared.db.NoteDb
import com.softartdev.notedelight.shared.db.NoteQueries
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import com.squareup.sqldelight.drivers.native.wrapConnection

class IosDatabaseHolder(
    key: String? = null,
    rekey: String? = null,
    name: String = DatabaseRepo.DB_NAME,
    schema: SqlDriver.Schema = NoteDb.Schema
) : DatabaseHolder() {
    private val configuration = DatabaseConfiguration(
        name = name,
        version = schema.version,
        create = { connection ->
            wrapConnection(connection) { schema.create(it) }
        },
        upgrade = { connection, oldVersion, newVersion ->
            wrapConnection(connection) { schema.migrate(it, oldVersion, newVersion) }
        },
        encryptionConfig = DatabaseConfiguration.Encryption(
            key = key,
            rekey = rekey,
        )
    )
    override val driver: SqlDriver = NativeSqliteDriver(configuration)
    override val noteDb: NoteDb = createQueryWrapper(driver)
    override val noteQueries: NoteQueries = noteDb.noteQueries

    override fun close() = driver.close()
}