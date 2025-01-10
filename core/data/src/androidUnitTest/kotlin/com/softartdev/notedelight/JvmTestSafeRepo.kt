package com.softartdev.notedelight

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.db.SqlDelightNoteDAO
import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.model.PlatformSQLiteThrowable
import com.softartdev.notedelight.repository.SafeRepo

/**
 * Encryption functions are mocked
 */
class JvmTestSafeRepo : SafeRepo() {
    @Volatile
    private var databaseHolder: JdbcDatabaseTestHolder? = buildDbIfNeed()

    override val databaseState: PlatformSQLiteState
        get() = TODO("Not yet implemented")

    override val noteDAO: NoteDAO
        get() = SqlDelightNoteDAO(databaseHolder?.noteQueries ?: throw PlatformSQLiteThrowable("DB is null"))

    override val dbPath: String
        get() = TODO("Not yet implemented")

    override fun buildDbIfNeed(passphrase: CharSequence): JdbcDatabaseTestHolder = synchronized(this) {
        var instance = databaseHolder
        if (instance == null) {
            instance = JdbcDatabaseTestHolder()
            databaseHolder = instance
        }
        return instance
    }

    override fun decrypt(oldPass: CharSequence) {
        closeDatabase()
        buildDbIfNeed()
    }

    override fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        closeDatabase()
        buildDbIfNeed(newPass)
    }

    override fun encrypt(newPass: CharSequence) {
        closeDatabase()
        buildDbIfNeed(newPass)
    }

    override fun execute(query: String): String? {
        val queryResult: QueryResult<String?> = buildDbIfNeed().driver.executeQuery(
            identifier = null,
            sql = query,
            parameters = 0,
            binders = null,
            mapper = this::map
        )
        return queryResult.value
    }

    private fun map(sqlCursor: SqlCursor): QueryResult<String?> = QueryResult.Value(
        value = if (sqlCursor.next().value) sqlCursor.getString(0) else null
    )

    override fun closeDatabase() = synchronized(this) {
        databaseHolder?.close()
        databaseHolder = null
    }
}