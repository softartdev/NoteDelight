package com.softartdev.notedelight.shared.db

import android.content.Context
import com.squareup.sqldelight.db.SqlDriver

class AndroidPlatformRepo(context: Context) : PlatformRepo() {

    private val driverFactory = DriverFactory(context)

    @Volatile
    override var noteDb: NoteDb? = buildDatabaseInstanceIfNeed()

    @Synchronized
    override fun buildDatabaseInstanceIfNeed(
        passphrase: CharSequence
    ): NoteDb = super.buildDatabaseInstanceIfNeed(passphrase)

    override fun createDriver(): SqlDriver = driverFactory.createDriver()
}