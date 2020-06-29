package com.softartdev.notedelight.shared.db

import com.squareup.sqldelight.db.SqlDriver
import kotlin.native.concurrent.AtomicReference

class IosPlatformRepo : PlatformRepo() {

    private val driverFactory = DriverFactory()

    private val _driver = AtomicReference<SqlDriver?>(null)
    override var driver: SqlDriver?
        get() = _driver.value
        set(value) {
            _driver.value = value
        }

    private val _noteDb = AtomicReference<NoteDb?>(buildDatabaseInstanceIfNeed())
    override var noteDb: NoteDb?
        get() = _noteDb.value
        set(value) {
            _noteDb.value = value
        }

    override fun createDriver(): SqlDriver = driverFactory.createDriver()
}