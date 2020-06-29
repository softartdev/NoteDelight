package com.softartdev.notedelight.shared.db

import com.squareup.sqldelight.db.SqlDriver

abstract class PlatformRepo {

    internal open var driver: SqlDriver? = null
    abstract var noteDb: NoteDb?

    val dbState: PlatformSQLiteState = PlatformSQLiteState.UNENCRYPTED

    val noteQueries: NoteQueries
        get() = noteDb?.noteQueries ?: throw PlatformSQLiteThrowable("DB is null")

    var relaunchFlowEmitter: (() -> Unit)? = null

    open fun buildDatabaseInstanceIfNeed(
        passphrase: CharSequence = ""
    ): NoteDb {
        var instance = noteDb
        if (instance == null) {
            val sqlDriver = createDriver()
            instance = createDatabase(sqlDriver)
            driver = sqlDriver
            noteDb = instance
        }
        return instance
    }

    abstract fun createDriver(): SqlDriver

    fun decrypt(oldPass: CharSequence) {
        TODO("Not yet implemented")
    }

    fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        TODO("Not yet implemented")
    }

    fun encrypt(newPass: CharSequence) {
        TODO("Not yet implemented")
    }

    fun closeDatabase() {
        noteDb = null
        driver?.close()
        driver = null
    }
}