package com.softartdev.notedelight.repository

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.room3.useWriterConnection
import androidx.sqlite.db.SupportSQLiteDatabase
import com.commonsware.cwac.saferoom.SQLCipherUtils
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.softartdev.notedelight.db.AndroidDatabaseHolder
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.db.NoteDatabase
import com.softartdev.notedelight.db.RoomDbHolder
import com.softartdev.notedelight.db.RoomNoteDAO
import com.softartdev.notedelight.model.PlatformSQLiteState

class AndroidSafeRepo(private val context: Context) : SafeRepo() {
    @Volatile
    private var databaseHolder: RoomDbHolder? = null

    private val noteDatabase: NoteDatabase
        get() = (requireNotNull(databaseHolder) as AndroidDatabaseHolder).noteDatabase

    override val databaseState: PlatformSQLiteState
        get() = when (SQLCipherUtils.getDatabaseState(context, DB_NAME)!!) {
            SQLCipherUtils.State.DOES_NOT_EXIST -> PlatformSQLiteState.DOES_NOT_EXIST
            SQLCipherUtils.State.UNENCRYPTED -> PlatformSQLiteState.UNENCRYPTED
            SQLCipherUtils.State.ENCRYPTED -> PlatformSQLiteState.ENCRYPTED
        }

    override val noteDAO: NoteDAO
        get() = RoomNoteDAO(this@AndroidSafeRepo::noteDatabase)

    override val dbPath: String
        get() = context.getDatabasePath(DB_NAME).absolutePath

    override suspend fun buildDbIfNeed(passphrase: CharSequence): RoomDbHolder = synchronized(this) {
        var instance = databaseHolder
        if (instance == null) {
            val passCopy = SpannableStringBuilder(passphrase) // threadsafe
            instance = AndroidDatabaseHolder(context, passCopy)
            databaseHolder = instance
        }
        return instance
    }

    override suspend fun decrypt(oldPass: CharSequence) {
        val originalFile = context.getDatabasePath(DB_NAME)

        val oldCopy = SpannableStringBuilder(oldPass) // threadsafe
        val passphrase = CharArray(oldCopy.length)
        oldCopy.getChars(0, oldCopy.length, passphrase, 0)

        closeDatabase()
        SQLCipherUtils.decrypt(context, originalFile, passphrase)

        buildDbIfNeed()
    }

    override suspend fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        val passphrase = SpannableStringBuilder(newPass) // threadsafe

        val androidDatabaseHolder = buildDbIfNeed(oldPass) as AndroidDatabaseHolder
        val supportSQLiteDatabase: SupportSQLiteDatabase = androidDatabaseHolder.openHelper.writableDatabase
        SafeHelperFactory.rekey(supportSQLiteDatabase, passphrase)

        closeDatabase()
        buildDbIfNeed(newPass)
    }

    override suspend fun encrypt(newPass: CharSequence) {
        val passphrase = SpannableStringBuilder(newPass) // threadsafe

        closeDatabase()
        SQLCipherUtils.encrypt(context, DB_NAME, passphrase)

        buildDbIfNeed(newPass)
    }

    override suspend fun execute(query: String): String? {
        buildDbIfNeed()
        return noteDatabase.useWriterConnection { connection ->
            connection.usePrepared(query) { statement ->
                if (statement.step()) statement.getText(0) else null
            }
        }
    }

    override suspend fun closeDatabase() = synchronized(this) {
        databaseHolder?.close()
        databaseHolder = null
    }
}
