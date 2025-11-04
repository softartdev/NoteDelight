package com.softartdev.notedelight.repository

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.db.SqlDelightNoteDAO
import com.softartdev.notedelight.db.TestWebDatabaseHolder
import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.util.CoroutineDispatchers

/**
 * Test-specific SafeRepo for wasmJs that uses the default SQLDelight worker
 * instead of the custom OPFS worker to avoid webpack resolution issues.
 */
class WebTestSafeRepo(private val coroutineDispatchers: CoroutineDispatchers) : SafeRepo() {

    private var dbHolder: TestWebDatabaseHolder? = null

    override val databaseState: PlatformSQLiteState
        get() = PlatformSQLiteState.UNENCRYPTED

    override val noteDAO: NoteDAO
        get() = SqlDelightNoteDAO({ dbHolder!!.noteQueries }, coroutineDispatchers)

    override val dbPath: String
        get() = "test-database"

    override suspend fun buildDbIfNeed(passphrase: CharSequence): TestWebDatabaseHolder {
        var instance = dbHolder
        if (instance == null) {
            instance = TestWebDatabaseHolder()
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
