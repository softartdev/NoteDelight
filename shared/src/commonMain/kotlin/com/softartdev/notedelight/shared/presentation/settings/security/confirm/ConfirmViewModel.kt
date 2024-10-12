package com.softartdev.notedelight.shared.presentation.settings.security.confirm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.usecase.crypt.ChangePasswordUseCase
import com.softartdev.notedelight.shared.util.CoroutineDispatchers
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ConfirmViewModel (
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val router: Router,
    private val coroutineDispatchers: CoroutineDispatchers,
) : ViewModel() {
    private val mutableStateFlow: MutableStateFlow<ConfirmResult> = MutableStateFlow(
        value = ConfirmResult.InitState
    )
    val resultStateFlow: MutableStateFlow<ConfirmResult> = mutableStateFlow

    fun conformCheck(
        password: CharSequence,
        repeatPassword: CharSequence
    ) = viewModelScope.launch(context = coroutineDispatchers.io) {
        mutableStateFlow.value = ConfirmResult.Loading
        try {
            when {
                password.toString() != repeatPassword.toString() -> {
                    mutableStateFlow.value = ConfirmResult.PasswordsNoMatchError
                }
                password.isEmpty() -> {
                    mutableStateFlow.value = ConfirmResult.EmptyPasswordError
                }
                else -> {
                    changePasswordUseCase(null, password)
                    navigateUp()
                }
            }
        } catch (e: Throwable) {
            Napier.e("❌", e)
            mutableStateFlow.value = ConfirmResult.Error(e.message)
        }
    }

    fun navigateUp() = viewModelScope.launch(context = coroutineDispatchers.main) {
        router.popBackStack()
    }
}
