package com.softartdev.notedelight.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.softartdev.notedelight.shared.base.KmmViewModel
import com.softartdev.notedelight.shared.database.DatabaseRepo
import com.softartdev.notedelight.shared.database.JdbcDbRepo
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
import org.koin.java.KoinJavaComponent.get

actual val repoModule: Module = module {
    single<DatabaseRepo> { JdbcDbRepo() }
}

actual val viewModelModule: Module = module {
    factory { SplashViewModel(get()) }
    factory { SignInViewModel(get()) }
    factory { MainViewModel(get()) }
    factory { NoteViewModel(get()) }
    factory { EditTitleViewModel(get()) }
    factory { SettingsViewModel(get()) }
    factory { EnterViewModel(get()) }
    factory { ConfirmViewModel(get()) }
    factory { ChangeViewModel(get()) }
}

@Composable
actual inline fun <reified T : KmmViewModel> getViewModel(): T = remember { get(T::class.java) }