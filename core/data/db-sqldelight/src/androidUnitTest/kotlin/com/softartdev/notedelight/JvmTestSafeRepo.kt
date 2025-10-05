package com.softartdev.notedelight

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.db.SqlDelightNoteDAO
import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.model.PlatformSQLiteThrowable
import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.util.CoroutineDispatchers

/**
 * Encryption functions are mocked
 */
class JvmTestSafeRepo(private val coroutineDispatchers: CoroutineDispatchers) : SafeRepo() {
    @Volatile
    private var databaseHolder: JdbcDatabaseTestHolder? = null

    override val databaseState: PlatformSQLiteState
        get() = TODO("Not yet implemented")

    override val noteDAO: NoteDAO
        get() = SqlDelightNoteDAO(
            noteQueries = databaseHolder?.noteQueries ?: throw PlatformSQLiteThrowable("DB is null"),
            coroutineDispatchers = coroutineDispatchers
        )

    override val dbPath: String
        get() = TODO("Not yet implemented")

    override suspend fun buildDbIfNeed(passphrase: CharSequence): JdbcDatabaseTestHolder {
        var instance = databaseHolder
        if (instance == null) {
            instance = JdbcDatabaseTestHolder()
            instance.createSchema()
            databaseHolder = instance
        }
        return instance
    }

    override suspend fun decrypt(oldPass: CharSequence) {
        closeDatabase()
        buildDbIfNeed()
    }

    override suspend fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        closeDatabase()
        buildDbIfNeed(newPass)
    }

    override suspend fun encrypt(newPass: CharSequence) {
        closeDatabase()
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