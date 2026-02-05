package com.softartdev.notedelight.usecase.settings

import com.softartdev.notedelight.repository.DatabaseFileTransfer
import com.softartdev.notedelight.repository.SafeRepo

class ImportDatabaseUseCase(private val safeRepo: SafeRepo) {

    suspend operator fun invoke(sourcePath: String) {
        safeRepo.closeDatabase()
        DatabaseFileTransfer.copyDatabase(sourcePath, safeRepo.dbPath)
    }
}
