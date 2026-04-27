package com.softartdev.notedelight.di

import com.softartdev.notedelight.interactor.BiometricAuthService
import com.softartdev.notedelight.interactor.IosBiometricAuthService
import com.softartdev.notedelight.repository.FileRepo
import com.softartdev.notedelight.repository.IosFileRepo
import com.softartdev.notedelight.repository.IosSafeRepo
import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.usecase.settings.AppVersionUseCase
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val repoModule: Module = module {
    singleOf(::IosSafeRepo) bind SafeRepo::class
    singleOf(::IosFileRepo) bind FileRepo::class
}

actual fun Module.factoryOfAppVersionUseCase(): KoinDefinition<AppVersionUseCase> = factoryOf(
    constructor = ::AppVersionUseCase
)

actual fun Module.singleOfBiometricAuthService(): KoinDefinition<BiometricAuthService> =
    factoryOf(constructor = ::IosBiometricAuthService)
