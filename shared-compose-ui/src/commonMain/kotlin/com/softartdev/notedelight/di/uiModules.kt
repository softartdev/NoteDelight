package com.softartdev.notedelight.di

import com.softartdev.notedelight.navigation.RouterImpl
import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.util.CoroutineDispatchers
import com.softartdev.notedelight.shared.util.CoroutineDispatchersImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val uiModules: List<Module>
    get() = listOf(navigationModule, utilModule)

val navigationModule = module {
    single<Router> { RouterImpl() }
}

val utilModule = module {
    single<CoroutineDispatchers> { CoroutineDispatchersImpl() }
}