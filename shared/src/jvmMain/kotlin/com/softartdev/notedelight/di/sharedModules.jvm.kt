package com.softartdev.notedelight.di

import com.softartdev.notedelight.repository.JvmSafeRepo
import com.softartdev.notedelight.repository.SafeRepo
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val repoModule: Module = module {
    singleOf(::JvmSafeRepo) bind SafeRepo::class
}
