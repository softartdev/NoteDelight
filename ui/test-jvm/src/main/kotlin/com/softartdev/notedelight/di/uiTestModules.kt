package com.softartdev.notedelight.di

import com.softartdev.notedelight.UiThreadRouter
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.navigation.RouterImpl
import com.softartdev.notedelight.util.CoroutineDispatchers
import com.softartdev.notedelight.util.CoroutineDispatchersImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val uiTestModules: List<Module>
    get() = listOf(navigationTestModule, utilTestModule)

val navigationTestModule = module {
    single<Router> { UiThreadRouter(router = RouterImpl()) }
}

val utilTestModule = module {
    single<CoroutineDispatchers> { CoroutineDispatchersImpl() }
}
