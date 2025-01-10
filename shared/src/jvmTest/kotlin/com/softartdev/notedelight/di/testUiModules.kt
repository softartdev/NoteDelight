@file:OptIn(ExperimentalCoroutinesApi::class)

package com.softartdev.notedelight.di

import com.softartdev.notedelight.CoroutineDispatchersStub
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.navigation.RouterStub
import com.softartdev.notedelight.util.CoroutineDispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.koin.core.module.Module
import org.koin.dsl.module

val uiModules: List<Module>
    get() = listOf(navigationModule, utilModule)

val navigationModule = module {
    single<Router> { RouterStub() }
}

val utilModule = module {
    single<CoroutineDispatchers> {
        CoroutineDispatchersStub(testDispatcher = UnconfinedTestDispatcher())
    }
}
