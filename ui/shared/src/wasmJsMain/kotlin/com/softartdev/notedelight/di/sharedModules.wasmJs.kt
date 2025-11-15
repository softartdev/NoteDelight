package com.softartdev.notedelight.di

import com.softartdev.notedelight.repository.FileRepo
import com.softartdev.notedelight.repository.WasmJsFileRepo
import com.softartdev.notedelight.repository.WebSafeRepo
import com.softartdev.notedelight.repository.SafeRepo
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val repoModule: Module = module {
    singleOf(::WebSafeRepo) bind SafeRepo::class
    singleOf(::WasmJsFileRepo) bind FileRepo::class
}
