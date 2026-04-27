package com.softartdev.notedelight.di

import com.softartdev.notedelight.interactor.BiometricAuthService
import com.softartdev.notedelight.interactor.WasmJsBiometricAuthService
import com.softartdev.notedelight.repository.FileRepo
import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.repository.WasmJsFileRepo
import com.softartdev.notedelight.repository.WebSafeRepo
import com.softartdev.notedelight.usecase.settings.AppVersionUseCase
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val repoModule: Module = module {
    singleOf(::WebSafeRepo) bind SafeRepo::class
    singleOf(::WasmJsFileRepo) bind FileRepo::class
}

actual fun Module.factoryOfAppVersionUseCase(): KoinDefinition<AppVersionUseCase> = factoryOf(
    constructor = ::AppVersionUseCase
)

actual fun Module.singleOfBiometricAuthService(): KoinDefinition<BiometricAuthService> = factoryOf(
    constructor = ::WasmJsBiometricAuthService
)
