package com.softartdev.notedelight.di

import com.softartdev.notedelight.interactor.AdaptiveInteractor
import com.softartdev.notedelight.interactor.AutofillInteractor
import com.softartdev.notedelight.interactor.AutofillInteractorImpl
import com.softartdev.notedelight.interactor.BiometricInteractor
import com.softartdev.notedelight.interactor.LocaleInteractor
import com.softartdev.notedelight.interactor.AndroidBiometricInteractor
import com.softartdev.notedelight.interactor.SnackbarInteractor
import com.softartdev.notedelight.interactor.SnackbarInteractorImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val interactorModule: Module = module {
    singleOf(::AdaptiveInteractor)
    factoryOf<AutofillInteractor>(::AutofillInteractorImpl)
    singleOf<SnackbarInteractor>(::SnackbarInteractorImpl)
    singleOf(::LocaleInteractor)
    single<BiometricInteractor> { AndroidBiometricInteractor(get()) }
}
