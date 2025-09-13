package com.softartdev.notedelight.repository

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import com.softartdev.notedelight.db.FilePathResolver
import com.softartdev.notedelight.db.JdbcDatabaseHolder
import com.softartdev.notedelight.db.JvmCipherUtils
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.db.SqlDelightNoteDAO
import com.softartdev.notedelight.model.PlatformSQLiteState

class JvmSafeRepo : SafeRepo() {
    @Volatile
    private var databaseHolder: JdbcDatabaseHolder? = null

    override val databaseState: PlatformSQLiteState
        get() = JvmCipherUtils.getDatabaseState()

    override val noteDAO: NoteDAO
        get() = SqlDelightNoteDAO(buildDbIfNeed().noteQueries)

    override val dbPath: String
        get() = FilePathResolver().invoke()

    override fun buildDbIfNeed(passphrase: CharSequence): JdbcDatabaseHolder {
        var instance = databaseHolder
        if (instance == null) {
            instance = JdbcDatabaseHolder(passphrase)
            databaseHolder = instance
        }
        return instance
    }

    override fun decrypt(oldPass: CharSequence) {
        closeDatabase()
        JdbcDatabaseHolder(oldPass, "").close()
        buildDbIfNeed()
    }

    override fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        closeDatabase()
        JdbcDatabaseHolder(oldPass, newPass).close()
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

    override fun encrypt(newPass: CharSequence) {
        closeDatabase()
        JdbcDatabaseHolder("", newPass).close()
        buildDbIfNeed(newPass)
    }

    override fun closeDatabase() = synchronized(this) {
        databaseHolder?.close()
        databaseHolder = null
    }
}