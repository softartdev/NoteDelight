package com.softartdev.notedelight.di

import androidx.compose.runtime.Composable
import com.softartdev.notedelight.shared.base.KmmViewModel
import com.softartdev.notedelight.shared.database.AndroidDbRepo
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
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

actual val repoModule: Module = module {
    single<DatabaseRepo> { AndroidDbRepo(get()) }
}

actual val viewModelModule: Module = module {
    viewModel { SplashViewModel(get()) }
    viewModel { SignInViewModel(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { NoteViewModel(get()) }
    viewModel { EditTitleViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { EnterViewModel(get()) }
    viewModel { ConfirmViewModel(get()) }
    viewModel { ChangeViewModel(get()) }
}

@Composable
actual inline fun <reified T : KmmViewModel> getViewModel(): T = org.koin.androidx.compose.getViewModel()