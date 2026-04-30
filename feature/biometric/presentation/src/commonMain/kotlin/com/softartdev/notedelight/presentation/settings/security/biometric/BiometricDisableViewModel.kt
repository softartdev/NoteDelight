package com.softartdev.notedelight.presentation.settings.security.biometric

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.usecase.biometric.DisableBiometricUseCase
import com.softartdev.notedelight.util.CoroutineDispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BiometricDisableViewModel(
    private val router: Router,
    private val coroutineDispatchers: CoroutineDispatchers,
) : ViewModel() {

    fun disableBiometricAndNavBack() = viewModelScope.launch {
        withContext(coroutineDispatchers.io) {
            DisableBiometricUseCase.dialogChannel.send(true)
        }
        router.popBackStack()
    }

    fun doNotDisableBiometricAndNavBack() = viewModelScope.launch {
        withContext(coroutineDispatchers.io) {
            DisableBiometricUseCase.dialogChannel.send(false)
        }
        router.popBackStack()
    }
}
