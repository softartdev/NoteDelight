package com.softartdev.notedelight

import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.repository.SafeRepo
import io.github.aakira.napier.Napier
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.inject

object DbTestEncryptor : () -> Unit {

    const val PASSWORD = "password"

    override fun invoke() = runBlocking {
        val safeRepo: SafeRepo by inject(SafeRepo::class.java)
        while (safeRepo.databaseState == PlatformSQLiteState.DOES_NOT_EXIST) {
            safeRepo.buildDbIfNeed()
            val count: Long = safeRepo.noteDAO.count()
            Napier.d("notes count = $count")
            Thread.sleep(1000)
            Napier.d("databaseState = ${safeRepo.databaseState.name}")
        }
        safeRepo.encrypt(StringBuilder(PASSWORD))
        safeRepo.closeDatabase()
        Napier.d("databaseState = ${safeRepo.databaseState.name}")
    }
}