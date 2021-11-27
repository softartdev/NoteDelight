package com.softartdev.notedelight.di

import androidx.compose.runtime.Composable
import com.softartdev.notedelight.shared.base.KmmViewModel
import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.shared.data.NoteUseCase
import com.softartdev.notedelight.shared.database.DatabaseRepo
import org.koin.core.module.Module
import org.koin.dsl.module

val allModules: List<Module>
    get() = repoModule + useCaseModule + viewModelModule

/**
Provide the [DatabaseRepo]
 */
expect val repoModule: Module

val useCaseModule: Module = module {
    single { CryptUseCase(get()) }
    single { NoteUseCase(get()) }
}

expect val viewModelModule: Module

@Composable
expect inline fun <reified T : KmmViewModel> getViewModel(): T