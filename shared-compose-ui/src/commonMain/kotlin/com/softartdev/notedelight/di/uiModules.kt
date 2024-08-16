package com.softartdev.notedelight.di

import com.softartdev.notedelight.navigation.RouterImpl
import com.softartdev.notedelight.shared.navigation.Router
import org.koin.dsl.module

val navigationModule = module {
    single<Router> { RouterImpl() }
}
