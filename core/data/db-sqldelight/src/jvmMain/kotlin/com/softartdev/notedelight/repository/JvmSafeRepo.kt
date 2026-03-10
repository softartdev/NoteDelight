package com.softartdev.notedelight.repository

import co.touchlab.kermit.Logger
import com.softartdev.notedelight.db.FilePathResolver
import com.softartdev.notedelight.db.JdbcDatabaseHolder
import com.softartdev.notedelight.db.JvmCipherUtils
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.db.SqlDelightNoteDAO
import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.util.CoroutineDispatchers
import java.sql.DriverManager
import java.util.Properties

class JvmSafeRepo(private val coroutineDispatchers: CoroutineDispatchers) : SafeRepo() {
    @Volatile
    private var databaseHolder: JdbcDatabaseHolder? = null

    override val databaseState: PlatformSQLiteState
        get() = JvmCipherUtils.getDatabaseState(dbPath)

    override val noteDAO: NoteDAO
        get() = SqlDelightNoteDAO({ databaseHolder!!.noteQueries }, coroutineDispatchers)

    override val dbPath: String
        get() = FilePathResolver().invoke()

    override suspend fun buildDbIfNeed(passphrase: CharSequence): JdbcDatabaseHolder {
        var instance = databaseHolder
        if (instance == null) {
            val properties = Properties()
            if (passphrase.isNotEmpty()) properties["password"] = StringBuilder(passphrase).toString()
            instance = JdbcDatabaseHolder(properties)
            instance.createSchema()
            databaseHolder = instance
        }
        return instance
    }

    override suspend fun decrypt(oldPass: CharSequence) {
        closeDatabase()
        JvmCipherUtils.decrypt(
            password = StringBuilder(oldPass).toString(),
            dbName = dbPath
        )
        buildDbIfNeed()
    }

    override suspend fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        decrypt(oldPass)
        encrypt(newPass)
    }

    override suspend fun execute(query: String): String? {
        val holder = buildDbIfNeed()
        val url = holder.jdbcUrl
        val props = Properties()
        var connection: java.sql.Connection? = null
        try {
            connection = DriverManager.getConnection(url, props)
            val stmt = connection.createStatement()
            val hasResultSet = stmt.execute(query)
            val result = if (hasResultSet) {
                val rs = stmt.resultSet
                if (rs.next()) rs.getString(1) else null
            } else {
                null
            }
            stmt.close()
            if (result != null) return result
            val mcStmt = connection.createStatement()
            val mcHasResult = mcStmt.execute("SELECT sqlite3mc_version();")
            val mcResult = if (mcHasResult) {
                val rs = mcStmt.resultSet
                if (rs.next()) rs.getString(1) else null
            } else {
                null
            }
            mcStmt.close()
            return mcResult
        } catch (e: Exception) {
            Logger.withTag("JvmSafeRepo").e(e) { "Error executing query: $query" }
            throw e
        } finally {
            connection?.close()
        }
    }

    override suspend fun encrypt(newPass: CharSequence) {
        closeDatabase()
        JvmCipherUtils.encrypt(
            password = StringBuilder(newPass).toString(),
            dbName = dbPath
        )
        buildDbIfNeed(newPass)
    }

    override suspend fun closeDatabase() = synchronized(this) {
        databaseHolder?.close()
        databaseHolder = null
    }
}