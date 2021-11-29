package com.softartdev.notedelight.shared.di

import com.softartdev.notedelight.shared.base.KmmViewModel
import org.koin.core.definition.Definition
import org.koin.core.instance.InstanceFactory
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.androidx.viewmodel.dsl.viewModel


actual inline fun <reified T : KmmViewModel> Module.viewModelFactory(
    qualifier: Qualifier?,
    noinline definition: Definition<T>
): Pair<Module, InstanceFactory<T>> = viewModel(qualifier, definition)