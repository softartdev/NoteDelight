package com.softartdev.notedelight.presentation.signin

import androidx.compose.ui.autofill.AutofillManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.navigation.AppNavGraph
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.usecase.crypt.CheckPasswordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private val checkPasswordUseCase: CheckPasswordUseCase,
    private val router: Router
) : ViewModel() {
    private val logger = Logger.withTag(this@SignInViewModel::class.simpleName.toString())
    private val mutableStateFlow: MutableStateFlow<SignInResult> = MutableStateFlow(
        value = SignInResult.ShowSignInForm
    )
    val stateFlow: StateFlow<SignInResult> = mutableStateFlow
    var autofillManager: AutofillManager? = null

    fun onAction(action: SignInAction) = when (action) {
        is SignInAction.OnSettingsClick -> router.navigateClearingBackStack(AppNavGraph.Settings)
        is SignInAction.OnSignInClick -> signIn(action.pass)
    }

    private fun signIn(pass: CharSequence) = viewModelScope.launch {
        mutableStateFlow.value = SignInResult.ShowProgress
        try {
            mutableStateFlow.value = when {
                pass.isEmpty() -> SignInResult.ShowEmptyPassError
                checkPasswordUseCase(pass) -> {
                    autofillManager?.commit()
                    router.navigateClearingBackStack(AppNavGraph.Main)
                    SignInResult.ShowSignInForm
                }
                else -> SignInResult.ShowIncorrectPassError
            }
        } catch (error: Throwable) {
            logger.e(error) { "Error during sign in" }
            autofillManager?.cancel()
            router.navigate(route = AppNavGraph.ErrorDialog(message = error.message))
            mutableStateFlow.value = SignInResult.ShowSignInForm
        }
    }
}
