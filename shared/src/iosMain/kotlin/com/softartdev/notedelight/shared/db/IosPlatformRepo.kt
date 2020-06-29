package com.softartdev.notedelight.shared.db

import com.squareup.sqldelight.db.SqlDriver
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.freeze

class IosPlatformRepo : PlatformRepo() {

    private val driverFactory = DriverFactory()

    private val _driver = AtomicReference<SqlDriver?>(null)
    override var driver: SqlDriver?
        get() = _driver.value
        set(value) {
            _driver.value = value.freeze()
        }

    private val _noteDb = AtomicReference<NoteDb?>(buildDatabaseInstanceIfNeed())
    override var noteDb: NoteDb?
        get() = _noteDb.value
        set(value) {
            _noteDb.value = value.freeze()
        }

    override fun createDriver(): SqlDriver = driverFactory.createDriver()
}