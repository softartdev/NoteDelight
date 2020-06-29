package com.softartdev.notedelight.shared.db

import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.flow.collect

class PlatformCryptUseCase(
    private val platformRepo: PlatformRepo
) {
    val dbIsEncrypted: Boolean get() = platformRepo.dbState != PlatformSQLiteState.ENCRYPTED
    
    suspend fun checkPassword(pass: CharSequence): Boolean = try {
        platformRepo.closeDatabase()
        val passCharArray = CharArray(pass.length, pass::get)
        val passphrase = String(passCharArray) // threadsafe
        platformRepo.buildDatabaseInstanceIfNeed(passphrase)
        platformRepo.noteQueries//TODO remove if no need (after tests for sign in)
            .getAll()
            .asFlow()
            .collect { it.executeAsOneOrNull() }
        true
    } catch (throwable: Throwable) {
        print(throwable.message)//TODO log stack trace
        false
    }

    fun changePassword(oldPass: CharSequence?, newPass: CharSequence?) {
        if (dbIsEncrypted) {
            requireNotNull(oldPass)
            if (newPass.isNullOrEmpty()) {
                platformRepo.decrypt(oldPass)
            } else {
                platformRepo.rekey(oldPass, newPass)
            }
        } else {
            requireNotNull(newPass)
            platformRepo.encrypt(newPass)
        }
        platformRepo.relaunchFlowEmitter?.invoke()
    }
}