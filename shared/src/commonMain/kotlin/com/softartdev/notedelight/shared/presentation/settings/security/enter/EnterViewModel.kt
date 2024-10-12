package com.softartdev.notedelight.shared.presentation.settings.security.enter

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

class EnterViewModel (
    private val checkPasswordUseCase: CheckPasswordUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val router: Router,
    private val coroutineDispatchers: CoroutineDispatchers,
) : ViewModel() {
    private val mutableStateFlow: MutableStateFlow<EnterResult> = MutableStateFlow(
        value = EnterResult.InitState
    )
    val resultStateFlow: StateFlow<EnterResult> = mutableStateFlow

    fun enterCheck(password: CharSequence) = viewModelScope.launch(context = coroutineDispatchers.io) {
        mutableStateFlow.value = EnterResult.Loading
        try {
            when {
                password.isEmpty() -> {
                    mutableStateFlow.value = EnterResult.EmptyPasswordError
                }
                checkPasswordUseCase(password) -> {
                    changePasswordUseCase(password, null)
                    navigateUp()
                }
                else -> {
                    mutableStateFlow.value = EnterResult.IncorrectPasswordError
                }
            }
        } catch (e: Throwable) {
            Napier.e("❌", e)
            mutableStateFlow.value = EnterResult.Error(e.message)
        }
    }

    fun navigateUp() = viewModelScope.launch(context = coroutineDispatchers.main) {
        router.popBackStack()
    }
}
