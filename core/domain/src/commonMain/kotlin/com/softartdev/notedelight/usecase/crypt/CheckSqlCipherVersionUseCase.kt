package com.softartdev.notedelight.usecase.crypt

import com.softartdev.notedelight.repository.SafeRepo

class CheckSqlCipherVersionUseCase(private val safeRepo: SafeRepo) : () -> String? {

    override fun invoke(): String? = safeRepo.execute(query = "PRAGMA cipher_version;")
}
