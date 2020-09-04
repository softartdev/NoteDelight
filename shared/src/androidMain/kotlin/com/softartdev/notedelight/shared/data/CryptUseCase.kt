package com.softartdev.notedelight.shared.data

import android.text.SpannableStringBuilder
import com.softartdev.notedelight.shared.database.PlatformSQLiteState
import com.softartdev.notedelight.shared.database.SafeRepo
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.first
import timber.log.Timber

class CryptUseCase(
        private val safeRepo: SafeRepo
) {
    fun dbIsEncrypted(): Boolean = when (safeRepo.databaseState) {
        PlatformSQLiteState.ENCRYPTED -> true
        PlatformSQLiteState.UNENCRYPTED -> false
        PlatformSQLiteState.DOES_NOT_EXIST -> false
    }

    suspend fun checkPassword(pass: CharSequence): Boolean = try {
        safeRepo.closeDatabase()
        val passphrase = SpannableStringBuilder(pass) // threadsafe
        safeRepo.buildDatabaseInstanceIfNeed(passphrase)
        safeRepo.noteQueries.getAll().asFlow().mapToList().first()//TODO remove if no need (after tests for sign in)
        true
    } catch (e: Exception) {
        Timber.e(e)
        false
    }

    fun changePassword(oldPass: CharSequence?, newPass: CharSequence?) {
        if (dbIsEncrypted()) {
            requireNotNull(oldPass)
            if (newPass.isNullOrEmpty()) {
                safeRepo.decrypt(oldPass)
            } else {
                safeRepo.rekey(oldPass, newPass)
            }
        } else {
            requireNotNull(newPass)
            safeRepo.encrypt(newPass)
        }
        safeRepo.relaunchFlowEmitter?.invoke()
    }
}