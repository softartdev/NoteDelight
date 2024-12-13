package com.softartdev.notedelight.shared.domain.usecase

import com.softartdev.notedelight.shared.db.SafeRepo

class CheckSqlCipherVersionUseCase(private val safeRepo: SafeRepo) {

    operator fun invoke(): String? {
        return safeRepo.databaseState.name
    }
}
