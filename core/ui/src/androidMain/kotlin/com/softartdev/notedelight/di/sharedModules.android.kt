package com.softartdev.notedelight.di

import android.content.Context
import com.softartdev.notedelight.interactor.AndroidBiometricAuthService
import com.softartdev.notedelight.interactor.BiometricAuthService
import com.softartdev.notedelight.repository.AndroidFileRepo
import com.softartdev.notedelight.repository.AndroidSafeRepo
import com.softartdev.notedelight.repository.FileRepo
import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.usecase.settings.AppVersionUseCase
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val repoModule: Module = module {
    singleOf(::AndroidSafeRepo) bind SafeRepo::class
    singleOf(::AndroidFileRepo) bind FileRepo::class
}

actual fun Module.factoryOfAppVersionUseCase(): KoinDefinition<AppVersionUseCase> =
    factoryOf<AppVersionUseCase, Context>(constructor = ::AppVersionUseCase)

actual fun Module.singleOfBiometricAuthService(): KoinDefinition<BiometricAuthService> =
    factoryOf<BiometricAuthService, Context>(constructor = ::AndroidBiometricAuthService)
