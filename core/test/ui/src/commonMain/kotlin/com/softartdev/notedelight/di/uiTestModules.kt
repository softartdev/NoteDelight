package com.softartdev.notedelight.di

import com.softartdev.notedelight.UiThreadRouter
import com.softartdev.notedelight.interactor.BiometricInteractor
import com.softartdev.notedelight.interactor.TestBiometricInteractor
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.navigation.RouterImpl
import com.softartdev.notedelight.ui.settings.detail.DatabaseFilePicker
import com.softartdev.notedelight.ui.settings.detail.TestDatabaseFilePicker
import org.koin.core.module.Module
import org.koin.dsl.module

val uiTestModules: List<Module>
    get() = listOf(navigationTestModule, interactorModule, utilModule, backupTestModule, biometricTestModule)

val navigationTestModule = module {
    single<Router> { UiThreadRouter(router = RouterImpl()) }
}

val backupTestModule = module {
    single<DatabaseFilePicker> { TestDatabaseFilePicker() }
}

val biometricTestModule = module {
    single { TestBiometricInteractor() }
    single<BiometricInteractor> { get<TestBiometricInteractor>() }
}
