package com.softartdev.notedelight.shared.di

import com.softartdev.notedelight.shared.db.NoteDAO
import com.softartdev.notedelight.shared.db.SafeRepo
import com.softartdev.notedelight.shared.presentation.main.MainViewModel
import com.softartdev.notedelight.shared.presentation.note.DeleteViewModel
import com.softartdev.notedelight.shared.presentation.note.NoteViewModel
import com.softartdev.notedelight.shared.presentation.note.SaveViewModel
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
import com.softartdev.notedelight.shared.usecase.note.DeleteNoteUseCase
import com.softartdev.notedelight.shared.usecase.note.SaveNoteUseCase
import com.softartdev.notedelight.shared.usecase.note.UpdateTitleUseCase
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
