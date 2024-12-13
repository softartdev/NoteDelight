package com.softartdev.notedelight.shared.di

import com.softartdev.notedelight.shared.domain.repository.NoteDAO
import com.softartdev.notedelight.shared.domain.repository.SafeRepository
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
import com.softartdev.notedelight.shared.domain.usecase.crypt.ChangePasswordUseCase
import com.softartdev.notedelight.shared.domain.usecase.crypt.CheckPasswordUseCase
import com.softartdev.notedelight.shared.domain.usecase.crypt.CheckSqlCipherVersionUseCase
import com.softartdev.notedelight.shared.domain.usecase.note.CreateNoteUseCase
import com.softartdev.notedelight.shared.domain.usecase.note.DeleteNoteUseCase
import com.softartdev.notedelight.shared.domain.usecase.note.SaveNoteUseCase
import com.softartdev.notedelight.shared.domain.usecase.note.UpdateTitleUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val sharedModules: List<Module>
    get() = repoModule + daoModule + useCaseModule + viewModelModule

/**
Provide the [SafeRepository]
 */
expect val repoModule: Module

val daoModule: Module = module {
    factory<NoteDAO> { get<SafeRepository>().noteDAO }
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
