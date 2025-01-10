package com.softartdev.notedelight.di

import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.shared.db.NoteQueries
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.verify.definition
import org.koin.test.verify.injectedParameters
import org.koin.test.verify.verify

@KoinExperimentalAPI
class KoinModulesJvmTest : KoinTest {

    private val allModules: Array<Module> = (sharedModules + uiModules).toTypedArray()

    private val appModule: Module = module {
        includes(*allModules)
    }

    @Test
    fun verifyKoinModules() = appModule.verify(
        injections = injectedParameters(
            definition<NoteDAO>(NoteQueries::class),
        )
    )
}
