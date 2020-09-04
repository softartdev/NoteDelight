package com.softartdev.notedelight.shared.data

import android.content.Context
import android.text.SpannableStringBuilder
import com.commonsware.cwac.saferoom.SQLCipherUtils
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.softartdev.notedelight.shared.database.AndroidDatabaseHolder
import com.softartdev.notedelight.shared.database.DatabaseHolder
import com.softartdev.notedelight.shared.db.NoteQueries

class SafeRepo(
        private val context: Context
) {
    @Volatile
    private var databaseHolder: DatabaseHolder? = buildDatabaseInstanceIfNeed()

    val databaseState: SQLCipherUtils.State
        get() = SQLCipherUtils.getDatabaseState(context, DB_NAME)

    val noteQueries: NoteQueries
        get() = databaseHolder?.noteQueries ?: throw SafeSQLiteException("DB is null")

    var relaunchFlowEmitter: (() -> Unit)? = null

    fun buildDatabaseInstanceIfNeed(
            passphrase: CharSequence = ""
    ): DatabaseHolder = synchronized(this) {
        var instance = databaseHolder
        if (instance == null) {
            val passCopy = SpannableStringBuilder(passphrase) // threadsafe
            instance = AndroidDatabaseHolder(context, passCopy)
            databaseHolder = instance
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

        val androidDatabaseHolder = buildDatabaseInstanceIfNeed(oldPass) as AndroidDatabaseHolder
        val supportSQLiteDatabase = androidDatabaseHolder.openDatabase
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
        databaseHolder?.close()
        databaseHolder = null
    }

    companion object {
        const val DB_NAME = "notes.db"
    }
}