package com.softartdev.notedelight.shared.di

import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.navigation.RouterStub
import org.koin.dsl.module

val navigationModule = module {
    single<Router> { RouterStub() }
}
