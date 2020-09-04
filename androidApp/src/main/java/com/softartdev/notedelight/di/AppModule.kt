package com.softartdev.notedelight.di

import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.shared.data.NoteUseCase
import com.softartdev.notedelight.shared.database.SafeRepo
import com.softartdev.notedelight.ui.main.MainActivity
import com.softartdev.notedelight.ui.main.MainViewModel
import com.softartdev.notedelight.ui.note.NoteActivity
import com.softartdev.notedelight.ui.note.NoteViewModel
import com.softartdev.notedelight.ui.settings.SettingsFragment
import com.softartdev.notedelight.ui.settings.SettingsViewModel
import com.softartdev.notedelight.ui.settings.security.change.ChangePasswordDialog
import com.softartdev.notedelight.ui.settings.security.change.ChangeViewModel
import com.softartdev.notedelight.ui.settings.security.confirm.ConfirmPasswordDialog
import com.softartdev.notedelight.ui.settings.security.confirm.ConfirmViewModel
import com.softartdev.notedelight.ui.settings.security.enter.EnterPasswordDialog
import com.softartdev.notedelight.ui.settings.security.enter.EnterViewModel
import com.softartdev.notedelight.ui.signin.SignInActivity
import com.softartdev.notedelight.ui.signin.SignInViewModel
import com.softartdev.notedelight.ui.splash.SplashActivity
import com.softartdev.notedelight.ui.splash.SplashViewModel
import com.softartdev.notedelight.ui.title.EditTitleDialog
import com.softartdev.notedelight.ui.title.EditTitleViewModel
import com.softartdev.notedelight.util.PreferencesHelper
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { PreferencesHelper(get()) }
    single { SafeRepo(get()) }
    single { CryptUseCase(get()) }
    single { NoteUseCase(get()) }
}

val mvvmModule = module {
    scope<SplashActivity> {
        viewModel { SplashViewModel(get()) }
    }
    scope<SignInActivity> {
        viewModel { SignInViewModel(get()) }
    }
    scope<MainActivity> {
        viewModel { MainViewModel(get()) }
    }
    scope<NoteActivity> {
        viewModel { NoteViewModel(get()) }
    }
    scope<EditTitleDialog> {
        viewModel { EditTitleViewModel(get()) }
    }
    scope<SettingsFragment> {
        viewModel { SettingsViewModel(get()) }
    }
    scope<EnterPasswordDialog> {
        viewModel { EnterViewModel(get()) }
    }
    scope<ConfirmPasswordDialog> {
        viewModel { ConfirmViewModel(get()) }
    }
    scope<ChangePasswordDialog> {
        viewModel { ChangeViewModel(get()) }
    }
}