package com.softartdev.notedelight

import com.softartdev.notedelight.shared.PlatformSQLiteState
import com.softartdev.notedelight.shared.db.SafeRepo
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.koin.mp.KoinPlatformTools

object DbTestEncryptor : () -> Unit {

    const val PASSWORD = "password"

    override fun invoke() {
//        val safeRepo: SafeRepo by inject(SafeRepo::class.java)
        val safeRepo: SafeRepo = KoinPlatformTools.defaultContext().get().get()
        while (safeRepo.databaseState == PlatformSQLiteState.DOES_NOT_EXIST) {
            safeRepo.buildDbIfNeed()
//            Thread.sleep(1000)
            runBlocking { delay(1000) }
            Napier.d("databaseState = ${safeRepo.databaseState.name}")
        }
        safeRepo.encrypt(PASSWORD)
        safeRepo.closeDatabase()
        Napier.d("databaseState = ${safeRepo.databaseState.name}")
    }
}