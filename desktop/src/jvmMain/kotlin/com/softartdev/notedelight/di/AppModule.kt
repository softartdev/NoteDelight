package com.softartdev.notedelight.di

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

interface AppModule {

    val dbRepo: DatabaseRepo
    val cryptUseCase: CryptUseCase
    val noteUseCase: NoteUseCase

    val splashViewModel: SplashViewModel
    val signInViewModel: SignInViewModel
    val mainViewModel: MainViewModel
    val noteViewModel: NoteViewModel
    val editTitleViewModel: EditTitleViewModel
    val settingsViewModel: SettingsViewModel
    val enterViewModel: EnterViewModel
    val confirmViewModel: ConfirmViewModel
    val changeViewModel: ChangeViewModel
}