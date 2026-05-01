package com.softartdev.notedelight.presentation.settings.security.biometric

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.interactor.BiometricInteractor
import com.softartdev.notedelight.interactor.BiometricPlatformWrapper
import com.softartdev.notedelight.interactor.BiometricResult
import com.softartdev.notedelight.interactor.SnackbarInteractor
import com.softartdev.notedelight.interactor.SnackbarMessage
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.presentation.settings.security.FieldLabel
import com.softartdev.notedelight.usecase.crypt.CheckPasswordUseCase
import com.softartdev.notedelight.util.CoroutineDispatchers
import com.softartdev.notedelight.util.CountingIdlingRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BiometricEnrollViewModel(
    private val checkPasswordUseCase: CheckPasswordUseCase,
    private val biometricInteractor: BiometricInteractor,
    private val snackbarInteractor: SnackbarInteractor,
    private val router: Router,
    private val coroutineDispatchers: CoroutineDispatchers,
) : ViewModel() {
    private val logger = Logger.withTag(this@BiometricEnrollViewModel::class.simpleName.toString())

    private val mutableStateFlow: MutableStateFlow<BiometricEnrollResult> =
        MutableStateFlow(BiometricEnrollResult())
    val stateFlow: StateFlow<BiometricEnrollResult> = mutableStateFlow

    fun onAction(action: BiometricEnrollAction) = when (action) {
        is BiometricEnrollAction.Cancel -> cancel()
        is BiometricEnrollAction.OnEditPassword -> onEditPassword(action.password)
        is BiometricEnrollAction.TogglePasswordVisibility -> togglePasswordVisibility()
        is BiometricEnrollAction.OnEnrollClick -> enroll(
            title = action.title,
            subtitle = action.subtitle,
            negativeButton = action.negativeButton,
            biometricPlatformWrapper = action.biometricPlatformWrapper,
        )
    }

    private fun onEditPassword(password: String) = mutableStateFlow.update { result ->
        return@update result.copy(
            isError = false,
            fieldLabel = FieldLabel.ENTER_PASSWORD,
            password = password
        )
    }

    private fun togglePasswordVisibility() = viewModelScope.launch {
        mutableStateFlow.update(BiometricEnrollResult::togglePasswordVisibility)
    }

    private fun enroll(
        title: String,
        subtitle: String,
        negativeButton: String,
        biometricPlatformWrapper: BiometricPlatformWrapper,
    ) = viewModelScope.launch(context = coroutineDispatchers.io) {
        CountingIdlingRes.increment()
        mutableStateFlow.update(BiometricEnrollResult::showLoading)
        try {
            val password: String = mutableStateFlow.value.password
            when {
                password.isEmpty() -> {
                    mutableStateFlow.update { it.copy(fieldLabel = FieldLabel.EMPTY_PASSWORD) }
                    mutableStateFlow.update(BiometricEnrollResult::showError)
                }
                checkPasswordUseCase(password) -> {
                    val result: BiometricResult = biometricInteractor.encryptAndStorePassword(
                        password = password,
                        title = title,
                        subtitle = subtitle,
                        negativeButton = negativeButton,
                        biometricPlatformWrapper = biometricPlatformWrapper,
                    )
                    when (result) {
                        is BiometricResult.Success -> withContext(coroutineDispatchers.main) {
                            router.popBackStack()
                        }
                        else -> {
                            val resultMessage: String = when (result) {
                                is BiometricResult.Error -> result.message
                                else -> result.toString()
                            }
                            logger.e { resultMessage }
                            snackbarInteractor.showMessage(SnackbarMessage.Simple(resultMessage))
                        }
                    }
                }
                else -> {
                    mutableStateFlow.update { it.copy(fieldLabel = FieldLabel.INCORRECT_PASSWORD) }
                    mutableStateFlow.update(BiometricEnrollResult::showError)
                }
            }
        } catch (e: Throwable) {
            logger.e(e) { "Error enrolling biometric" }
            e.message?.let { snackbarInteractor.showMessage(SnackbarMessage.Simple(it)) }
        } finally {
            mutableStateFlow.update(BiometricEnrollResult::hideLoading)
            CountingIdlingRes.decrement()
        }
    }

    private fun cancel() = viewModelScope.launch { router.popBackStack() }
}
