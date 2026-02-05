package com.softartdev.notedelight.usecase.settings

import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.repository.DatabaseFileTransfer
import com.softartdev.notedelight.repository.SafeRepo

class ExportDatabaseUseCase(private val safeRepo: SafeRepo) {

    suspend operator fun invoke(destinationPath: String) {
        val isEncrypted = safeRepo.databaseState == PlatformSQLiteState.ENCRYPTED
        when {
            isEncrypted -> safeRepo.execute("PRAGMA wal_checkpoint(FULL)")
            else -> safeRepo.closeDatabase()
        }
        DatabaseFileTransfer.copyDatabase(safeRepo.dbPath, destinationPath)

        if (!isEncrypted) safeRepo.buildDbIfNeed()
    }
}
