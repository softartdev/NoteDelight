package com.softartdev.notedelight.shared.presentation.settings.security.change

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.usecase.crypt.ChangePasswordUseCase
import com.softartdev.notedelight.shared.usecase.crypt.CheckPasswordUseCase
import com.softartdev.notedelight.shared.util.CoroutineDispatchers
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChangeViewModel(
    private val checkPasswordUseCase: CheckPasswordUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val router: Router,
    private val coroutineDispatchers: CoroutineDispatchers,
) : ViewModel() {
    private val mutableStateFlow: MutableStateFlow<ChangeResult> = MutableStateFlow(
        value = ChangeResult.InitState
    )
    val resultStateFlow: StateFlow<ChangeResult> = mutableStateFlow

    fun checkChange(
        oldPassword: CharSequence,
        newPassword: CharSequence,
        repeatNewPassword: CharSequence
    ) = viewModelScope.launch(context = coroutineDispatchers.io) {
        mutableStateFlow.value = ChangeResult.Loading
        try {
            when {
                oldPassword.isEmpty() -> {
                    mutableStateFlow.value = ChangeResult.OldEmptyPasswordError
                }
                newPassword.isEmpty() -> {
                    mutableStateFlow.value = ChangeResult.NewEmptyPasswordError
                }
                newPassword.toString() != repeatNewPassword.toString() -> {
                    mutableStateFlow.value = ChangeResult.PasswordsNoMatchError
                }
                checkPasswordUseCase(oldPassword) -> {
                    changePasswordUseCase(oldPassword, newPassword)
                    navigateUp()
                }
                else -> {
                    mutableStateFlow.value = ChangeResult.IncorrectPasswordError
                }
            }
        } catch (e: Throwable) {
            Napier.e("❌", e)
            mutableStateFlow.value = ChangeResult.Error(e.message)
        }
    }

    fun navigateUp() = viewModelScope.launch(context = coroutineDispatchers.main) {
        router.popBackStack()
    }
}
