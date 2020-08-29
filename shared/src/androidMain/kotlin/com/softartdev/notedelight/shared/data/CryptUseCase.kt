package com.softartdev.notedelight.shared.data

import android.text.SpannableStringBuilder
import com.commonsware.cwac.saferoom.SQLCipherUtils
import kotlinx.coroutines.flow.first
import timber.log.Timber

class CryptUseCase(
        private val safeRepo: SafeRepo
) {

    fun dbIsEncrypted(): Boolean = when (safeRepo.databaseState) {
        SQLCipherUtils.State.ENCRYPTED -> true
        SQLCipherUtils.State.UNENCRYPTED -> false
        SQLCipherUtils.State.DOES_NOT_EXIST -> false
    }

    suspend fun checkPassword(pass: CharSequence): Boolean = try {
        safeRepo.closeDatabase()
        val passphrase = SpannableStringBuilder(pass) // threadsafe
        safeRepo.buildDatabaseInstanceIfNeed(passphrase)
        safeRepo.noteDao.getNotes().first()//TODO remove if no need (after tests for sign in)
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