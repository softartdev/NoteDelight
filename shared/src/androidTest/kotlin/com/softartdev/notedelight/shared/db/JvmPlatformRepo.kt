package com.softartdev.notedelight.shared.db

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver

class JvmPlatformRepo : PlatformRepo() {

    @Volatile
    override var noteDb: NoteDb? = buildDatabaseInstanceIfNeed()

    @Synchronized
    override fun buildDatabaseInstanceIfNeed(
        passphrase: CharSequence
    ): NoteDb = super.buildDatabaseInstanceIfNeed(passphrase)

    override fun createDriver(): SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
}