package com.softartdev.notedelight

import co.touchlab.kermit.Logger
import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.repository.SafeRepo
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.inject

object DbTestEncryptor : () -> Unit {
    private val logger = Logger.withTag(this@DbTestEncryptor::class.simpleName.toString())
    const val PASSWORD = "password"

    override fun invoke() = runBlocking {
        val safeRepo: SafeRepo by inject(SafeRepo::class.java)
        while (safeRepo.databaseState == PlatformSQLiteState.DOES_NOT_EXIST) {
            safeRepo.buildDbIfNeed()
            val count: Long = safeRepo.noteDAO.count()
            logger.d { "notes count = $count" }
            Thread.sleep(1000)
            logger.d { "databaseState = ${safeRepo.databaseState.name}" }
        }
        safeRepo.encrypt(StringBuilder(PASSWORD))
        safeRepo.closeDatabase()
        logger.d { "databaseState = ${safeRepo.databaseState.name}" }
    }
}