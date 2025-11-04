package com.softartdev.notedelight.di

import com.softartdev.notedelight.interactor.AdaptiveInteractor
import com.softartdev.notedelight.interactor.SnackbarInteractor
import com.softartdev.notedelight.interactor.SnackbarInteractorImpl
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.navigation.RouterImpl
import com.softartdev.notedelight.util.CoroutineDispatchers
import com.softartdev.notedelight.util.CoroutineDispatchersImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val uiModules: List<Module>
    get() = listOf(navigationModule, interactorModule, utilModule)

val navigationModule = module {
    singleOf<Router>(::RouterImpl)
}

val interactorModule = module {
    singleOf(::AdaptiveInteractor)
    singleOf<SnackbarInteractor>(::SnackbarInteractorImpl)
}

val utilModule = module {
    singleOf<CoroutineDispatchers>(::CoroutineDispatchersImpl)
}