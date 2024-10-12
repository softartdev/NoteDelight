@file:OptIn(ExperimentalCoroutinesApi::class)

package com.softartdev.notedelight.shared.di

import com.softartdev.notedelight.shared.CoroutineDispatchersStub
import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.navigation.RouterStub
import com.softartdev.notedelight.shared.util.CoroutineDispatchers
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
