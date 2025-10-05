package com.softartdev.notedelight.repository

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import com.softartdev.notedelight.db.IosCipherUtils
import com.softartdev.notedelight.db.IosDatabaseHolder
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.db.SqlDelightNoteDAO
import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.util.CoroutineDispatchers

class IosSafeRepo(private val coroutineDispatchers: CoroutineDispatchers) : SafeRepo() {

    private var dbHolder: IosDatabaseHolder? = null

    override val databaseState: PlatformSQLiteState
        get() = IosCipherUtils.getDatabaseState(DB_NAME)

    override val noteDAO: NoteDAO
        get() = SqlDelightNoteDAO(dbHolder!!.noteQueries, coroutineDispatchers)

    override val dbPath: String
        get() = IosCipherUtils.getDatabasePath(DB_NAME)

    override suspend fun buildDbIfNeed(passphrase: CharSequence): IosDatabaseHolder {
        var instance = dbHolder
        if (instance == null) {
            val passCopy: String? = if (passphrase.isNotEmpty()) passphrase.toString() else null
            instance = IosDatabaseHolder(key = passCopy)
            instance.createSchema()
            dbHolder = instance
        }
        return instance
    }

    override suspend fun decrypt(oldPass: CharSequence) {
        closeDatabase()
        IosCipherUtils.decrypt(oldPass.toString(), DB_NAME)
        dbHolder = IosDatabaseHolder()
    }

    override suspend fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        closeDatabase()
        dbHolder = IosDatabaseHolder(key = oldPass.toString(), rekey = newPass.toString())
        dbHolder?.driver?.execute(null, "VACUUM;", 0)
    }

    override suspend fun encrypt(newPass: CharSequence) {
        closeDatabase()
        IosCipherUtils.encrypt(newPass.toString(), DB_NAME)
        dbHolder = IosDatabaseHolder(key = newPass.toString())
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

    override suspend fun closeDatabase() {
        dbHolder?.close()
        dbHolder = null
    }
}