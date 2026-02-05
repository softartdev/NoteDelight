package com.softartdev.notedelight.usecase.crypt

import co.touchlab.kermit.Logger
import com.softartdev.notedelight.repository.SafeRepo

class CheckPasswordUseCase(private val safeRepo: SafeRepo) {
    private val logger = Logger.withTag(this@CheckPasswordUseCase::class.simpleName.toString())

    @Throws(Throwable::class)
    suspend operator fun invoke(pass: CharSequence): Boolean = try {
        safeRepo.closeDatabase()
        val passphrase = StringBuilder(pass) // threadsafe
        safeRepo.buildDbIfNeed(passphrase)
        val count: Long = safeRepo.noteDAO.count()
        logger.i { "Checked pass on DB with $count notes" }
        true
    } catch (t: Throwable) {
        logger.i(t) { "Incorrect password" }
        false
    }
}
