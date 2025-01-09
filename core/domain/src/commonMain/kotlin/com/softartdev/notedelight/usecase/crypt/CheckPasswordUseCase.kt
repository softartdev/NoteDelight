package com.softartdev.notedelight.usecase.crypt

import com.softartdev.notedelight.repository.SafeRepo
import io.github.aakira.napier.Napier

class CheckPasswordUseCase(private val safeRepo: SafeRepo) {

    @Throws(Throwable::class)
    operator fun invoke(pass: CharSequence): Boolean = try {
        safeRepo.closeDatabase()
        val passphrase = StringBuilder(pass) // threadsafe
        safeRepo.buildDbIfNeed(passphrase)
        true
    } catch (t: Throwable) {
        Napier.i(message = "Incorrect password", throwable = t)
        false
    }
}