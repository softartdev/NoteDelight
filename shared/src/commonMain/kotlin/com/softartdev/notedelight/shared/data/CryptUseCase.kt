package com.softartdev.notedelight.shared.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import com.softartdev.notedelight.shared.PlatformSQLiteState
import com.softartdev.notedelight.shared.database.DatabaseRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.firstOrNull

class CryptUseCase(
    private val dbRepo: DatabaseRepo
) {
    fun dbIsEncrypted(): Boolean = when (dbRepo.databaseState) {
        PlatformSQLiteState.ENCRYPTED -> true
        PlatformSQLiteState.UNENCRYPTED -> false
        PlatformSQLiteState.DOES_NOT_EXIST -> false
    }

    @Throws(Throwable::class)
    suspend fun isDbEncrypted(): Boolean = dbIsEncrypted()

    suspend fun checkPassword(pass: CharSequence): Boolean = try {
        dbRepo.closeDatabase()
        val passphrase = StringBuilder(pass) // threadsafe
        dbRepo.buildDatabaseInstanceIfNeed(passphrase)
        dbRepo.noteQueries.getAll().asFlow().mapToList(Dispatchers.IO)
            .firstOrNull()//TODO remove if no need (after tests for sign in)
        true
    } catch (t: Throwable) {
        t.printStackTrace()
        false
    }

    fun changePassword(oldPass: CharSequence?, newPass: CharSequence?) {
        if (dbIsEncrypted()) {
            requireNotNull(oldPass)
            if (newPass.isNullOrEmpty()) {
                dbRepo.decrypt(oldPass)
            } else {
                dbRepo.rekey(oldPass, newPass)
            }
        } else {
            requireNotNull(newPass)
            dbRepo.encrypt(newPass)
        }
        dbRepo.relaunchFlowEmitter?.invoke()
    }

    fun dbCipherVersion(): String? {
        val dbHolder = dbRepo.buildDatabaseInstanceIfNeed()
        val queryResult: QueryResult<String?> = dbHolder.driver.executeQuery(
            identifier = null,
            sql = "PRAGMA cipher_version;",
            parameters = 0,
            binders = null,
            mapper = map@{ sqlCursor: SqlCursor ->
                return@map QueryResult.Value(
                    value = if (sqlCursor.next().value) sqlCursor.getString(0) else null
                )
            }
        )
        return queryResult.value
    }
}