package com.softartdev.notedelight.shared.data

import android.content.Context
import android.text.SpannableStringBuilder
import com.commonsware.cwac.saferoom.SQLCipherUtils
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.softartdev.notedelight.shared.database.NoteDao
import com.softartdev.notedelight.shared.database.NoteDatabase
import com.softartdev.notedelight.shared.database.NoteDatabaseImpl

class SafeRepo(
        private val context: Context
) {

    @Volatile
    private var noteDatabase: NoteDatabase? = buildDatabaseInstanceIfNeed()

    val databaseState: SQLCipherUtils.State
        get() = SQLCipherUtils.getDatabaseState(context, DB_NAME)

    val noteDao: NoteDao
        get() = noteDatabase?.noteDao() ?: throw SafeSQLiteException("DB is null")

    var relaunchFlowEmitter: (() -> Unit)? = null

    fun buildDatabaseInstanceIfNeed(
            passphrase: CharSequence = ""
    ): NoteDatabase = synchronized(this) {
        var instance = noteDatabase
        if (instance == null) {
            val passCopy = SpannableStringBuilder(passphrase) // threadsafe
            instance = NoteDatabaseImpl(context, passCopy)
            noteDatabase = instance
        }
        return instance
    }

    fun decrypt(oldPass: CharSequence) {
        val originalFile = context.getDatabasePath(DB_NAME)

        val oldCopy = SpannableStringBuilder(oldPass) // threadsafe
        val passphrase = CharArray(oldCopy.length)
        oldCopy.getChars(0, oldCopy.length, passphrase, 0)

        closeDatabase()
        SQLCipherUtils.decrypt(context, originalFile, passphrase)

        buildDatabaseInstanceIfNeed()
    }

    fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        val passphrase = SpannableStringBuilder(newPass) // threadsafe

        val supportSQLiteDatabase = buildDatabaseInstanceIfNeed(oldPass).openHelper.writableDatabase
        SafeHelperFactory.rekey(supportSQLiteDatabase, passphrase)

        buildDatabaseInstanceIfNeed(newPass)
    }

    fun encrypt(newPass: CharSequence) {
        val passphrase = SpannableStringBuilder(newPass) // threadsafe

        closeDatabase()
        SQLCipherUtils.encrypt(context, DB_NAME, passphrase)

        buildDatabaseInstanceIfNeed(newPass)
    }

    fun closeDatabase() = synchronized(this) {
        noteDatabase?.close()
        noteDatabase = null
    }

    companion object {
        const val DB_NAME = "notes.db"
    }
}