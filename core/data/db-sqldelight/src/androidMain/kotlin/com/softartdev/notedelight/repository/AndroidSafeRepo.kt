package com.softartdev.notedelight.repository

import android.content.Context
import android.database.sqlite.SQLiteException
import android.text.SpannableStringBuilder
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import com.commonsware.cwac.saferoom.SQLCipherUtils
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.softartdev.notedelight.db.AndroidDatabaseHolder
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.db.SqlDelightDbHolder
import com.softartdev.notedelight.db.SqlDelightNoteDAO
import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.shared.db.NoteQueries
import com.softartdev.notedelight.util.CoroutineDispatchers

class AndroidSafeRepo(
    private val context: Context,
    private val coroutineDispatchers: CoroutineDispatchers
) : SafeRepo() {

    @Volatile
    private var databaseHolder: SqlDelightDbHolder? = null

    override val databaseState: PlatformSQLiteState
        get() = when (SQLCipherUtils.getDatabaseState(context, DB_NAME)!!) {
            SQLCipherUtils.State.DOES_NOT_EXIST -> PlatformSQLiteState.DOES_NOT_EXIST
            SQLCipherUtils.State.UNENCRYPTED -> PlatformSQLiteState.UNENCRYPTED
            SQLCipherUtils.State.ENCRYPTED -> PlatformSQLiteState.ENCRYPTED
        }

    override val noteDAO: NoteDAO
        get() = SqlDelightNoteDAO(::noteQueries, coroutineDispatchers)

    override val dbPath: String
        get() = context.getDatabasePath(DB_NAME).absolutePath

    private val noteQueries : NoteQueries
        get() = databaseHolder?.noteQueries ?: throw SQLiteException("dnHolder is null")

    override suspend fun buildDbIfNeed(passphrase: CharSequence): SqlDelightDbHolder {
        var instance = databaseHolder
        if (instance == null) {
            val passCopy = SpannableStringBuilder(passphrase) // threadsafe
            instance = AndroidDatabaseHolder(context, passCopy)
            instance.createSchema()
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
        val supportSQLiteDatabase = androidDatabaseHolder.openDatabase
        SafeHelperFactory.rekey(supportSQLiteDatabase, passphrase)

        buildDbIfNeed(newPass)
    }

    override suspend fun encrypt(newPass: CharSequence) {
        val passphrase = SpannableStringBuilder(newPass) // threadsafe

        closeDatabase()
        SQLCipherUtils.encrypt(context, DB_NAME, passphrase)

        buildDbIfNeed(newPass)
    }

    override suspend fun execute(query: String): String? {
        val queryResult: QueryResult<String?> = buildDbIfNeed().driver.executeQuery(
            identifier = null,
            sql = query,
            parameters = 0,
            binders = null,
            mapper = this::map
        )
        return queryResult.await()
    }

    private fun map(sqlCursor: SqlCursor): QueryResult<String?> = QueryResult.Value(
        value = if (sqlCursor.next().value) sqlCursor.getString(0) else null
    )

    override suspend fun closeDatabase() = synchronized(this) {
        databaseHolder?.close()
        databaseHolder = null
    }
}