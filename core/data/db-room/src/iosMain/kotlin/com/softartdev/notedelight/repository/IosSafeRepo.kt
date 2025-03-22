package com.softartdev.notedelight.repository

import com.softartdev.notedelight.db.IosCipherUtils
import com.softartdev.notedelight.db.IosDatabaseHolder
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.db.RoomNoteDAO
import com.softartdev.notedelight.model.PlatformSQLiteState

class IosSafeRepo : SafeRepo() {

    private var dbHolder: IosDatabaseHolder? = null

    override val databaseState: PlatformSQLiteState
        get() = IosCipherUtils.getDatabaseState(DB_NAME)

    override val noteDAO: NoteDAO
        get() = RoomNoteDAO(noteDatabase = buildDbIfNeed().noteDatabase)

    override val dbPath: String
        get() = IosCipherUtils.getDatabasePath(DB_NAME)

    override fun buildDbIfNeed(passphrase: CharSequence): IosDatabaseHolder {
        var instance = dbHolder
        if (instance == null) {
            val passCopy: String? = if (passphrase.isNotEmpty()) passphrase.toString() else null
            instance = IosDatabaseHolder(key = passCopy)
            dbHolder = instance
        }
        return instance
    }

    override fun decrypt(oldPass: CharSequence) {
        closeDatabase()
        IosCipherUtils.decrypt(oldPass.toString(), DB_NAME)
        dbHolder = IosDatabaseHolder()
    }

    override fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        closeDatabase()
        dbHolder = IosDatabaseHolder(key = oldPass.toString(), rekey = newPass.toString())
//        dbHolder?.driver?.execute(null, "VACUUM;", 0)
    }

    override fun encrypt(newPass: CharSequence) {
        closeDatabase()
        IosCipherUtils.encrypt(newPass.toString(), DB_NAME)
        dbHolder = IosDatabaseHolder(key = newPass.toString())
    }

    override fun execute(query: String): String? {
        return TODO()
    }

    override fun closeDatabase() {
        dbHolder?.close()
        dbHolder = null
    }
}