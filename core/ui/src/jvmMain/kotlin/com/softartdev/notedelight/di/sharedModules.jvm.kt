package com.softartdev.notedelight.di

import com.softartdev.notedelight.repository.FileRepo
import com.softartdev.notedelight.repository.JvmFileRepo
import com.softartdev.notedelight.repository.JvmSafeRepo
import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.usecase.settings.AppVersionUseCase
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val repoModule: Module = module {
    singleOf(::JvmSafeRepo) bind SafeRepo::class
    singleOf(::JvmFileRepo) bind FileRepo::class
}

actual fun Module.factoryOfAppVersionUseCase(): KoinDefinition<AppVersionUseCase> = factoryOf(
    constructor = ::AppVersionUseCase
)
