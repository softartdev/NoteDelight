package com.softartdev.notedelight.old.di

import android.content.Context
import com.softartdev.notedelight.old.util.PreferencesHelper
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.koin.core.logger.Level
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.category.CheckModuleTest
import org.koin.test.check.checkModules
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.Mockito.mock

@Category(CheckModuleTest::class)
class ModuleCheckTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger(level = Level.ERROR) // TODO revert to Level.DEBUG after update Koin version above 3.1.5
        modules(allAndroidModules)
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mock(clazz.java)
    }

    @Before
    fun setUp() {
        declareMock<Context>()
        declareMock<PreferencesHelper>()
    }

    @Test
    fun `check Koin modules`() = koinTestRule.koin.checkModules()
}