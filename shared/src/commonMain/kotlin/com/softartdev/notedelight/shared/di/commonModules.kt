package com.softartdev.notedelight.shared.di

import com.softartdev.notedelight.shared.db.SafeRepo
import com.softartdev.notedelight.shared.presentation.main.MainViewModel
import com.softartdev.notedelight.shared.presentation.note.NoteViewModel
import com.softartdev.notedelight.shared.presentation.settings.SettingsViewModel
import com.softartdev.notedelight.shared.presentation.settings.security.change.ChangeViewModel
import com.softartdev.notedelight.shared.presentation.settings.security.confirm.ConfirmViewModel
import com.softartdev.notedelight.shared.presentation.settings.security.enter.EnterViewModel
import com.softartdev.notedelight.shared.presentation.signin.SignInViewModel
import com.softartdev.notedelight.shared.presentation.splash.SplashViewModel
import com.softartdev.notedelight.shared.presentation.title.EditTitleViewModel
import com.softartdev.notedelight.shared.usecase.crypt.ChangePasswordUseCase
import com.softartdev.notedelight.shared.usecase.crypt.CheckPasswordUseCase
import com.softartdev.notedelight.shared.usecase.crypt.CheckSqlCipherVersionUseCase
import com.softartdev.notedelight.shared.usecase.note.CreateNoteUseCase
import com.softartdev.notedelight.shared.usecase.note.SaveNoteUseCase
import com.softartdev.notedelight.shared.usecase.note.UpdateTitleUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

val allModules: List<Module>
    get() = repoModule + daoModule + useCaseModule + viewModelModule

/**
Provide the [SafeRepo]
 */
expect val repoModule: Module

val daoModule: Module = module {
    factory { get<SafeRepo>().noteDAO }
}

val useCaseModule: Module = module {
    factory { ChangePasswordUseCase(get()) }
    factory { CheckPasswordUseCase(get()) }
    factory { CheckSqlCipherVersionUseCase(get()) }
    factory { CreateNoteUseCase(get()) }
    factory { SaveNoteUseCase(get()) }
    factory { UpdateTitleUseCase(get()) }
}

val viewModelModule: Module = module {
    viewModelFactory { SplashViewModel(get()) }
    viewModelFactory { SignInViewModel(get()) }
    viewModelFactory { MainViewModel(get(), get()) }
    viewModelFactory { NoteViewModel(get(), get(), get()) }
    viewModelFactory { EditTitleViewModel(get(), get()) }
    viewModelFactory { SettingsViewModel(get(), get()) }
    viewModelFactory { EnterViewModel(get(), get()) }
    viewModelFactory { ConfirmViewModel(get()) }
    viewModelFactory { ChangeViewModel(get(), get()) }
}
