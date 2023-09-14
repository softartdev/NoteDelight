package com.softartdev.notedelight

import com.softartdev.notedelight.shared.PlatformSQLiteState
import com.softartdev.notedelight.shared.database.DatabaseRepo
import io.github.aakira.napier.Napier
import org.koin.java.KoinJavaComponent.inject

object DbTestEncryptor : () -> Unit {

    const val PASSWORD = "password"

    override fun invoke() {
        val safeRepo: DatabaseRepo by inject(DatabaseRepo::class.java)
        while (safeRepo.databaseState == PlatformSQLiteState.DOES_NOT_EXIST) {
            safeRepo.buildDatabaseInstanceIfNeed()
            Thread.sleep(1000)
            Napier.d("databaseState = ${safeRepo.databaseState.name}")
        }
        safeRepo.encrypt(StringBuilder(PASSWORD))
        safeRepo.closeDatabase()
        Napier.d("databaseState = ${safeRepo.databaseState.name}")
    }
}