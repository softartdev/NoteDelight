package com.softartdev.notedelight.shared.di

import org.junit.Test
import org.koin.core.logger.Level
import org.koin.test.KoinTest
import org.koin.test.check.checkKoinModules

class KoinModulesJvmTest : KoinTest {

    @Test
    fun checkModulesTest() = checkKoinModules(
        modules = sharedModules + uiModules,
        appDeclaration = { printLogger(level = Level.DEBUG) }
    )
}