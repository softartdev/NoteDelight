package com.softartdev.notedelight.shared.di

import com.softartdev.notedelight.shared.db.IosSafeRepo
import com.softartdev.notedelight.shared.db.SafeRepo
import org.koin.core.module.Module
import org.koin.dsl.module

actual val repoModule: Module = module {
    single<SafeRepo> { IosSafeRepo() }
}