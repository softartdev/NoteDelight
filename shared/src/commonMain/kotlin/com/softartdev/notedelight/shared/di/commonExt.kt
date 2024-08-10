package com.softartdev.notedelight.shared.di

import androidx.lifecycle.ViewModel
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier


expect inline fun <reified T : ViewModel> Module.viewModelFactory(
    qualifier: Qualifier? = null,
    noinline definition: Definition<T>
): KoinDefinition<T>