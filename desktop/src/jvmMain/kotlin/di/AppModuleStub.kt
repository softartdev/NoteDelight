package di

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

class AppModuleStub : AppModule {
    override val dbRepo: DatabaseRepo
        get() = TODO("Not yet implemented")
    override val cryptUseCase: CryptUseCase
        get() = TODO("Not yet implemented")
    override val noteUseCase: NoteUseCase
        get() = TODO("Not yet implemented")
    override val splashViewModel: SplashViewModel
        get() = TODO("Not yet implemented")
    override val signInViewModel: SignInViewModel
        get() = TODO("Not yet implemented")
    override val mainViewModel: MainViewModel
        get() = TODO("Not yet implemented")
    override val noteViewModel: NoteViewModel
        get() = TODO("Not yet implemented")
    override val editTitleViewModel: EditTitleViewModel
        get() = TODO("Not yet implemented")
    override val settingsViewModel: SettingsViewModel
        get() = TODO("Not yet implemented")
    override val enterViewModel: EnterViewModel
        get() = TODO("Not yet implemented")
    override val confirmViewModel: ConfirmViewModel
        get() = TODO("Not yet implemented")
    override val changeViewModel: ChangeViewModel
        get() = TODO("Not yet implemented")
}