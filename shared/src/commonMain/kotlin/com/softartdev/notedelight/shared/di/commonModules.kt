package com.softartdev.notedelight.shared.di

import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.shared.data.NoteUseCase
import com.softartdev.notedelight.shared.database.DatabaseRepo
import com.softartdev.notedelight.shared.presentation.main.MainViewModel
import com.softartdev.notedelight.shared.presentation.note.NoteViewModel
import com.softartdev.notedelight.shared.presentation.settings.SettingsViewModel
import com.softartdev.notedelight.shared.presentation.settings.security.change.ChangeViewModel
import com.softartdev.notedelight.shared.presentation.settings.security.confirm.ConfirmViewModel
import com.softartdev.notedelight.shared.presentation.settings.security.enter.EnterViewModel
import com.softartdev.notedelight.shared.presentation.signin.SignInViewModel
import com.softartdev.notedelight.shared.presentation.splash.SplashViewModel
import com.softartdev.notedelight.shared.presentation.title.EditTitleViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val allModules: List<Module>
    get() = prefModule + repoModule + useCaseModule + viewModelModule

val prefModule: Module = module { /*TODO expect all platforms*/ }

/**
Provide the [DatabaseRepo]
 */
expect val repoModule: Module

val useCaseModule: Module = module {
    single { CryptUseCase(get()) }
    single { NoteUseCase(get()) }
}

val viewModelModule: Module = module {
    viewModelFactory { SplashViewModel(get()) }
    viewModelFactory { SignInViewModel(get()) }
    viewModelFactory { MainViewModel(get()) }
    viewModelFactory { NoteViewModel(get()) }
    viewModelFactory { EditTitleViewModel(get()) }
    viewModelFactory { SettingsViewModel(get()) }
    viewModelFactory { EnterViewModel(get()) }
    viewModelFactory { ConfirmViewModel(get()) }
    viewModelFactory { ChangeViewModel(get()) }
}
