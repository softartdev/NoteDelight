package com.softartdev.notedelight.di

import com.softartdev.notedelight.repository.AndroidFileRepo
import com.softartdev.notedelight.repository.AndroidSafeRepo
import com.softartdev.notedelight.repository.FileRepo
import com.softartdev.notedelight.repository.SafeRepo
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val repoModule: Module = module {
    singleOf(::AndroidSafeRepo) bind SafeRepo::class
    singleOf(::AndroidFileRepo) bind FileRepo::class
}
