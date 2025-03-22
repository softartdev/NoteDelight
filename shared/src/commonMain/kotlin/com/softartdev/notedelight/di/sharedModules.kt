package com.softartdev.notedelight.di

import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.presentation.main.MainViewModel
import com.softartdev.notedelight.presentation.note.DeleteViewModel
import com.softartdev.notedelight.presentation.note.NoteViewModel
import com.softartdev.notedelight.presentation.note.SaveViewModel
import com.softartdev.notedelight.presentation.settings.SettingsViewModel
import com.softartdev.notedelight.presentation.settings.security.change.ChangeViewModel
import com.softartdev.notedelight.presentation.settings.security.confirm.ConfirmViewModel
import com.softartdev.notedelight.presentation.settings.security.enter.EnterViewModel
import com.softartdev.notedelight.presentation.signin.SignInViewModel
import com.softartdev.notedelight.presentation.splash.SplashViewModel
import com.softartdev.notedelight.presentation.title.EditTitleViewModel
import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.usecase.crypt.*
import com.softartdev.notedelight.usecase.note.*
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val sharedModules: List<Module>
    get() = repoModule + daoModule + useCaseModule + viewModelModule

/**
Provide the [SafeRepo]
 */
expect val repoModule: Module

val daoModule: Module = module {
    factory<NoteDAO> { get<SafeRepo>().noteDAO }
}

val useCaseModule: Module = module {
    factoryOf(::ChangePasswordUseCase)
    factoryOf(::CheckPasswordUseCase)
    factoryOf(::CheckSqlCipherVersionUseCase)
    factoryOf(::CreateNoteUseCase)
    factoryOf(::SaveNoteUseCase)
    factoryOf(::DeleteNoteUseCase)
    factoryOf(::UpdateTitleUseCase)
}

val viewModelModule: Module = module {
    viewModelOf(::SplashViewModel)
    viewModelOf(::SignInViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::NoteViewModel)
    viewModelOf(::SaveViewModel)
    viewModelOf(::DeleteViewModel)
    viewModelOf(::EditTitleViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::EnterViewModel)
    viewModelOf(::ConfirmViewModel)
    viewModelOf(::ChangeViewModel)
}
