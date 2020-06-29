package com.softartdev.notedelight.shared.db

actual class PlatformRepo {

    private val driverFactory = DriverFactory()

    actual var noteDb: NoteDb? = buildDatabaseInstanceIfNeed()

    actual val dbState: PlatformSQLiteState = PlatformSQLiteState.UNENCRYPTED

    actual val noteQueries: NoteQueries
        get() = noteDb?.noteQueries ?: throw PlatformSQLiteThrowable("DB is null")

    actual var relaunchFlowEmitter: (() -> Unit)? = null

    actual fun buildDatabaseInstanceIfNeed(
        passphrase: CharSequence
    ): NoteDb {
        var instance = noteDb
        if (instance == null) {
            instance = createDatabase(driverFactory)
            noteDb = instance
        }
        return instance
    }

    actual fun decrypt(oldPass: CharSequence) {
        TODO("Not yet implemented")
    }

    actual fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        TODO("Not yet implemented")
    }

    actual fun encrypt(newPass: CharSequence) {
        TODO("Not yet implemented")
    }

    actual fun closeDatabase() {
        TODO("Not yet implemented")
    }

}