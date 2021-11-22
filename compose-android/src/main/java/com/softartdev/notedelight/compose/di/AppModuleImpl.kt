package com.softartdev.notedelight.compose.di

import android.content.Context
import com.softartdev.notedelight.di.AppModule
import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.shared.data.NoteUseCase
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

class AppModuleImpl(private val context: Context) : AppModule {

    override val dbRepo: DatabaseRepo by lazy { AndroidDbRepo(context) }
    override val cryptUseCase: CryptUseCase by lazy { CryptUseCase(dbRepo) }
    override val noteUseCase: NoteUseCase by lazy { NoteUseCase(dbRepo) }

    override val splashViewModel: SplashViewModel
        get() = SplashViewModel(cryptUseCase)
    override val signInViewModel: SignInViewModel
        get() = SignInViewModel(cryptUseCase)
    override val mainViewModel: MainViewModel
        get() = MainViewModel(noteUseCase)
    override val noteViewModel: NoteViewModel
        get() = NoteViewModel(noteUseCase)
    override val editTitleViewModel: EditTitleViewModel
        get() = EditTitleViewModel(noteUseCase)
    override val settingsViewModel: SettingsViewModel
        get() = SettingsViewModel(cryptUseCase)
    override val enterViewModel: EnterViewModel
        get() = EnterViewModel(cryptUseCase)
    override val confirmViewModel: ConfirmViewModel
        get() = ConfirmViewModel(cryptUseCase)
    override val changeViewModel: ChangeViewModel
        get() = ChangeViewModel(cryptUseCase)
}