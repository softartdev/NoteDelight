package com.softartdev.notedelight.usecase.crypt

import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.util.Platform
import com.softartdev.notedelight.util.platform

class CheckSqlCipherVersionUseCase(private val safeRepo: SafeRepo) : suspend () -> String? {

    override suspend fun invoke(): String? = safeRepo.execute(
        query = when (platform) {
            Platform.Android, Platform.IOS -> "PRAGMA cipher_version;"
            Platform.Desktop, Platform.Web -> "SELECT sqlite3mc_version();"
        }
    )
}
