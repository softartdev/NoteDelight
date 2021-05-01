package com.softartdev.notedelight.di

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.softartdev.notedelight.shared.test.util.MainCoroutineRule
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.koin.android.ext.koin.androidContext
import org.koin.test.KoinTest
import org.koin.test.category.CheckModuleTest
import org.koin.test.check.checkModules
import org.koin.test.mock.MockProviderRule
import org.mockito.Mockito.mock

@Category(CheckModuleTest::class)
class ModuleCheckTest : KoinTest {

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mock(clazz.java)
    }

    private val mockContext = mock(Context::class.java)

    @Test
    fun checkModules() = checkModules {
        androidContext(mockContext)
        modules(testModule + mvvmModule)
    }
}