package com.softartdev.notedelight.di

import com.softartdev.notedelight.UiThreadRouter
import com.softartdev.notedelight.interactor.AdaptiveInteractor
import com.softartdev.notedelight.interactor.LocaleInteractor
import com.softartdev.notedelight.interactor.SnackbarInteractor
import com.softartdev.notedelight.interactor.SnackbarInteractorImpl
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.navigation.RouterImpl
import com.softartdev.notedelight.util.CoroutineDispatchers
import com.softartdev.notedelight.util.CoroutineDispatchersImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val uiTestModules: List<Module>
    get() = listOf(navigationTestModule, interactorModule, utilTestModule)

val navigationTestModule = module {
    single<Router> { UiThreadRouter(router = RouterImpl()) }
}

val interactorModule: Module = module {
    singleOf(::AdaptiveInteractor)
    singleOf<SnackbarInteractor>(::SnackbarInteractorImpl)
    singleOf(::LocaleInteractor)
}

val utilTestModule = module {
    single<CoroutineDispatchers> { CoroutineDispatchersImpl() }
}
