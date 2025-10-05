package com.softartdev.notedelight.repository

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.db.SqlDelightNoteDAO
import com.softartdev.notedelight.db.WebDatabaseHolder
import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.util.CoroutineDispatchers

class WebSafeRepo(private val coroutineDispatchers: CoroutineDispatchers) : SafeRepo() {

    private var dbHolder: WebDatabaseHolder? = null

    override val databaseState: PlatformSQLiteState
        get() = PlatformSQLiteState.UNENCRYPTED

    override val noteDAO: NoteDAO
        get() = SqlDelightNoteDAO(dbHolder!!.noteQueries, coroutineDispatchers)

    override val dbPath: String
        get() = TODO("Not yet implemented")

    override suspend fun buildDbIfNeed(passphrase: CharSequence): WebDatabaseHolder {
        var instance = dbHolder
        if (instance == null) {
            instance = WebDatabaseHolder()
            instance.createSchema()
            dbHolder = instance
        }
        return instance
    }

    override suspend fun decrypt(oldPass: CharSequence) {
        TODO("Not yet implemented")
    }

    override suspend fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        TODO("Not yet implemented")
    }

    override suspend fun encrypt(newPass: CharSequence) {
        TODO("Not yet implemented")
    }

    override suspend fun execute(query: String): String? {
        val queryResult = buildDbIfNeed().driver.executeQuery(
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

    override suspend fun closeDatabase() {
        dbHolder?.close()
        dbHolder = null
    }
}
