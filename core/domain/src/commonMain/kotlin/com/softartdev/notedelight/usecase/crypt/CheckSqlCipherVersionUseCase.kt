package com.softartdev.notedelight.usecase.crypt

import com.softartdev.notedelight.repository.SafeRepo

class CheckSqlCipherVersionUseCase(private val safeRepo: SafeRepo) : suspend () -> String? {

    override suspend fun invoke(): String? = safeRepo.execute(query = "PRAGMA cipher_version;")
}
