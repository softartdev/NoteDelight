package com.softartdev.notedelight.repository

import androidx.room3.useWriterConnection
import com.softartdev.notedelight.db.IosCipherUtils
import com.softartdev.notedelight.db.IosDatabaseHolder
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.db.NoteDatabase
import com.softartdev.notedelight.db.RoomNoteDAO
import com.softartdev.notedelight.model.PlatformSQLiteState

class IosSafeRepo : SafeRepo() {

    private var dbHolder: IosDatabaseHolder? = null

    private val noteDatabase: NoteDatabase
        get() = requireNotNull(dbHolder).noteDatabase

    override val databaseState: PlatformSQLiteState
        get() = IosCipherUtils.getDatabaseState(DB_NAME)

    override val noteDAO: NoteDAO
        get() = RoomNoteDAO(this@IosSafeRepo::noteDatabase)

    override val dbPath: String
        get() = IosCipherUtils.getDatabasePath(DB_NAME)

    override suspend fun buildDbIfNeed(passphrase: CharSequence): IosDatabaseHolder {
        var instance = dbHolder
        if (instance == null) {
            IosCipherUtils.ensureDatabaseDir()
            val passCopy: String? = if (passphrase.isNotEmpty()) passphrase.toString() else null
            instance = IosDatabaseHolder(key = passCopy)
            dbHolder = instance
        }
        return instance
    }

    override suspend fun decrypt(oldPass: CharSequence) {
        closeDatabase()
        IosCipherUtils.decrypt(oldPass.toString(), DB_NAME)
        dbHolder = IosDatabaseHolder()
    }

    override suspend fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        closeDatabase()
        IosCipherUtils.rekey(oldPass.toString(), newPass.toString(), DB_NAME)
        dbHolder = IosDatabaseHolder(key = newPass.toString())
    }

    override suspend fun encrypt(newPass: CharSequence) {
        closeDatabase()
        IosCipherUtils.encrypt(newPass.toString(), DB_NAME)
        dbHolder = IosDatabaseHolder(key = newPass.toString())
    }

    override suspend fun execute(query: String): String? {
        buildDbIfNeed()
        return noteDatabase.useWriterConnection { connection ->
            connection.usePrepared(query) { statement ->
                if (statement.step()) statement.getText(0) else null
            }
        }
    }

    override suspend fun closeDatabase() {
        dbHolder?.close()
        dbHolder = null
    }

    override suspend fun deleteDatabase(): Boolean {
        closeDatabase()
        return IosCipherUtils.deleteDatabase()
    }
}
