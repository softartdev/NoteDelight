package com.softartdev.notedelight.shared.db

import android.content.Context
import android.text.SpannableStringBuilder
import com.commonsware.cwac.saferoom.SQLCipherUtils
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.softartdev.notedelight.shared.PlatformSQLiteState

class AndroidSafeRepo(private val context: Context) : SafeRepo() {
    @Volatile
    private var databaseHolder: DatabaseHolder? = null

    override val databaseState: PlatformSQLiteState
        get() = when (SQLCipherUtils.getDatabaseState(context, DB_NAME)!!) {
            SQLCipherUtils.State.DOES_NOT_EXIST -> PlatformSQLiteState.DOES_NOT_EXIST
            SQLCipherUtils.State.UNENCRYPTED -> PlatformSQLiteState.UNENCRYPTED
            SQLCipherUtils.State.ENCRYPTED -> PlatformSQLiteState.ENCRYPTED
        }

    override val noteDAO: NoteDAO
        get() = NoteDAO(buildDbIfNeed().noteQueries)

    override val dbPath: String
        get() = context.getDatabasePath(DB_NAME).absolutePath

    override fun buildDbIfNeed(passphrase: CharSequence): DatabaseHolder = synchronized(this) {
        var instance = databaseHolder
        if (instance == null) {
            val passCopy = SpannableStringBuilder(passphrase) // threadsafe
            instance = AndroidDatabaseHolder(context, passCopy)
            databaseHolder = instance
        }
        return instance
    }

    override fun decrypt(oldPass: CharSequence) {
        val originalFile = context.getDatabasePath(DB_NAME)

        val oldCopy = SpannableStringBuilder(oldPass) // threadsafe
        val passphrase = CharArray(oldCopy.length)
        oldCopy.getChars(0, oldCopy.length, passphrase, 0)

        closeDatabase()
        SQLCipherUtils.decrypt(context, originalFile, passphrase)

        buildDbIfNeed()
    }

    override fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        val passphrase = SpannableStringBuilder(newPass) // threadsafe

        val androidDatabaseHolder = buildDbIfNeed(oldPass) as AndroidDatabaseHolder
        val supportSQLiteDatabase = androidDatabaseHolder.openDatabase
        SafeHelperFactory.rekey(supportSQLiteDatabase, passphrase)

        buildDbIfNeed(newPass)
    }

    override fun encrypt(newPass: CharSequence) {
        val passphrase = SpannableStringBuilder(newPass) // threadsafe

        closeDatabase()
        SQLCipherUtils.encrypt(context, DB_NAME, passphrase)

        buildDbIfNeed(newPass)
    }

    override fun closeDatabase() = synchronized(this) {
        databaseHolder?.close()
        databaseHolder = null
    }
}