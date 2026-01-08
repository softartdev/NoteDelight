package com.softartdev.notedelight

import co.touchlab.kermit.Logger
import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.repository.SafeRepo
import kotlinx.coroutines.delay
import org.koin.mp.KoinPlatform
import kotlin.time.Duration.Companion.seconds

object DbTestEncryptor : suspend () -> Unit {
    private val logger = Logger.withTag(this@DbTestEncryptor::class.simpleName.toString())
    const val PASSWORD = "password"

    override suspend fun invoke() {
        val safeRepo: SafeRepo by KoinPlatform.getKoin().inject()
        while (safeRepo.databaseState == PlatformSQLiteState.DOES_NOT_EXIST) {
            safeRepo.buildDbIfNeed()
            val count: Long = safeRepo.noteDAO.count()
            logger.d { "notes count = $count" }
            delay(duration = 1.seconds)
            logger.d { "databaseState = ${safeRepo.databaseState.name}" }
        }
        safeRepo.encrypt(StringBuilder(PASSWORD))
        safeRepo.closeDatabase()
        logger.d { "databaseState = ${safeRepo.databaseState.name}" }
    }
}

