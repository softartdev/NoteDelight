package com.softartdev.notedelight.di

import com.softartdev.notedelight.shared.di.repoModule
import com.softartdev.notedelight.shared.di.useCaseModule
import com.softartdev.notedelight.shared.presentation.main.MainViewModel
import com.softartdev.notedelight.shared.presentation.note.NoteViewModel
import com.softartdev.notedelight.shared.presentation.settings.SettingsViewModel
import com.softartdev.notedelight.shared.presentation.settings.security.change.ChangeViewModel
import com.softartdev.notedelight.shared.presentation.settings.security.confirm.ConfirmViewModel
import com.softartdev.notedelight.shared.presentation.settings.security.enter.EnterViewModel
import com.softartdev.notedelight.shared.presentation.signin.SignInViewModel
import com.softartdev.notedelight.shared.presentation.splash.SplashViewModel
import com.softartdev.notedelight.shared.presentation.title.EditTitleViewModel
import com.softartdev.notedelight.ui.main.MainActivity
import com.softartdev.notedelight.ui.note.NoteActivity
import com.softartdev.notedelight.ui.settings.SettingsActivity
import com.softartdev.notedelight.ui.settings.SettingsFragment
import com.softartdev.notedelight.ui.settings.security.change.ChangePasswordDialog
import com.softartdev.notedelight.ui.settings.security.confirm.ConfirmPasswordDialog
import com.softartdev.notedelight.ui.settings.security.enter.EnterPasswordDialog
import com.softartdev.notedelight.ui.signin.SignInActivity
import com.softartdev.notedelight.ui.splash.SplashActivity
import com.softartdev.notedelight.ui.title.EditTitleDialog
import com.softartdev.notedelight.util.PreferencesHelper
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val allAndroidModules: List<Module>
    get() = prefAndroidModule + repoModule + useCaseModule + viewModelAndroidModule

val prefAndroidModule: Module = module {
    single { PreferencesHelper(get()) }
}

val viewModelAndroidModule = module {
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

        scope<EditTitleDialog> {
            viewModel { EditTitleViewModel(get()) }
        }
    }
    scope<SettingsActivity> {
        scope<SettingsFragment> {
            viewModel { SettingsViewModel(get()) }

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
    }
}