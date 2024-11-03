package com.softartdev.notedelight.shared.di

import com.softartdev.notedelight.shared.db.IosSafeRepo
import com.softartdev.notedelight.shared.db.SafeRepo
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val repoModule: Module = module {
    singleOf(::IosSafeRepo) bind SafeRepo::class
}