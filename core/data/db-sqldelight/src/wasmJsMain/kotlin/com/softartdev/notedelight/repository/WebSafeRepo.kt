package com.softartdev.notedelight.repository

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.db.SqlDelightNoteDAO
import com.softartdev.notedelight.db.WebDatabaseHolder
import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.util.CoroutineDispatchers

class WebSafeRepo(private val coroutineDispatchers: CoroutineDispatchers) : SafeRepo() {
    private val logger = Logger.withTag("WebSafeRepo")
    private var dbHolder: WebDatabaseHolder? = null

    override var databaseState: PlatformSQLiteState = PlatformSQLiteState.UNENCRYPTED
        private set

    override val noteDAO: NoteDAO
        get() = SqlDelightNoteDAO({ dbHolder!!.noteQueries }, coroutineDispatchers)

    override val dbPath: String = "database.db"

    override suspend fun buildDbIfNeed(passphrase: CharSequence): WebDatabaseHolder {
        var instance = dbHolder
        if (instance == null) {
            val key = passphrase.takeIf(CharSequence::isNotEmpty)?.toString()
            instance = WebDatabaseHolder(key)
            // PRAGMA key must be the FIRST statement after opening the database
            instance.applyKey()
            // createSchema() swallows exceptions, so we probe the database separately
            instance.createSchema()
            // Verify the database is actually readable (detects encrypted DB opened without key)
            if (!isDatabaseReadable(instance)) {
                if (passphrase.isEmpty()) {
                    logger.d { "Database not readable without key, assuming encrypted" }
                    instance.close()
                    databaseState = PlatformSQLiteState.ENCRYPTED
                    return instance
                }
                // Opened with a key but still not readable — wrong password or corrupt DB
                instance.close()
                throw IllegalStateException("Database is not readable with the provided key")
            }
            dbHolder = instance
            databaseState = when {
                passphrase.isEmpty() -> PlatformSQLiteState.UNENCRYPTED
                else -> PlatformSQLiteState.ENCRYPTED
            }
        }
        return instance
    }

    /**
     * Probes the database with a simple query to check if it is readable.
     * Returns false if the database is encrypted and was opened without the correct key.
     */
    private suspend fun isDatabaseReadable(holder: WebDatabaseHolder): Boolean = try {
        holder.driver.executeQuery(
            identifier = null,
            sql = "SELECT count(*) FROM sqlite_master",
            parameters = 0,
            binders = null,
            mapper = { cursor ->
                cursor.next()
                QueryResult.Value(cursor.getLong(0))
            }
        ).await()
        true
    } catch (t: Throwable) {
        logger.d(t) { "Database readability check failed" }
        false
    }

    override suspend fun encrypt(newPass: CharSequence) {
        logger.d { "Encrypting database" }
        val holder = dbHolder ?: buildDbIfNeed()
        val escapedKey = newPass.toString().replace("'", "''")
        // On an unencrypted database, PRAGMA rekey encrypts it in-place
        holder.driver.execute(null, "PRAGMA rekey = '$escapedKey'", 0, null).await()
        logger.d { "PRAGMA rekey executed, closing and reopening with key" }
        closeDatabase()
        buildDbIfNeed(newPass)
    }

    override suspend fun decrypt(oldPass: CharSequence) {
        logger.d { "Decrypting database" }
        val holder = dbHolder ?: buildDbIfNeed(oldPass)
        // On an encrypted database (already opened with key), PRAGMA rekey = '' removes encryption
        holder.driver.execute(null, "PRAGMA rekey = ''", 0, null).await()
        logger.d { "PRAGMA rekey='' executed, closing and reopening without key" }
        closeDatabase()
        buildDbIfNeed()
    }

    override suspend fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        logger.d { "Rekeying database" }
        val holder = dbHolder ?: buildDbIfNeed(oldPass)
        val escapedKey = newPass.toString().replace("'", "''")
        // On an encrypted database (already opened with old key), PRAGMA rekey changes the key
        holder.driver.execute(null, "PRAGMA rekey = '$escapedKey'", 0, null).await()
        logger.d { "PRAGMA rekey executed with new key, closing and reopening" }
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

    override suspend fun closeDatabase() {
        dbHolder?.close()
        dbHolder = null
    }
}
