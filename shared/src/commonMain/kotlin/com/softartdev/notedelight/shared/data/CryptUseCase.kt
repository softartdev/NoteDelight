package com.softartdev.notedelight.shared.data

import com.softartdev.notedelight.shared.database.DatabaseRepo
import com.softartdev.notedelight.shared.database.PlatformSQLiteState
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.first

class CryptUseCase(
        private val dbRepo: DatabaseRepo
) {
    fun dbIsEncrypted(): Boolean = when (dbRepo.databaseState) {
        PlatformSQLiteState.ENCRYPTED -> true
        PlatformSQLiteState.UNENCRYPTED -> false
        PlatformSQLiteState.DOES_NOT_EXIST -> false
    }

    suspend fun checkPassword(pass: CharSequence): Boolean = try {
        dbRepo.closeDatabase()
        val passphrase = StringBuilder(pass) // threadsafe
        dbRepo.buildDatabaseInstanceIfNeed(passphrase)
        dbRepo.noteQueries.getAll().asFlow().mapToList().first()//TODO remove if no need (after tests for sign in)
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
}