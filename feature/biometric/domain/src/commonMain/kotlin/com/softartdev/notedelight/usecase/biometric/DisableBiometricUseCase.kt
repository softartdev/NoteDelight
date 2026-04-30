package com.softartdev.notedelight.usecase.biometric

import com.softartdev.notedelight.interactor.BiometricInteractor
import kotlinx.coroutines.channels.Channel

class DisableBiometricUseCase(
    private val biometricInteractor: BiometricInteractor,
) : suspend () -> Unit {

    override suspend fun invoke() = biometricInteractor.clearStoredPassword()

    companion object {
        val dialogChannel: Channel<Boolean> by lazy { return@lazy Channel() }
    }
}
