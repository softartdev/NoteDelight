package com.softartdev.notedelight.interactor

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics
import platform.LocalAuthentication.LAErrorUserCancel
import platform.LocalAuthentication.LAErrorUserFallback
import kotlin.coroutines.resume

class IosBiometricAuthService : BiometricAuthService {

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun isBiometricAvailable(): Boolean = memScoped {
        val authContext = LAContext()
        val errorPtr = alloc<ObjCObjectVar<platform.Foundation.NSError?>>()
        authContext.canEvaluatePolicy(
            policy = LAPolicyDeviceOwnerAuthenticationWithBiometrics,
            error = errorPtr.ptr
        )
    }

    override suspend fun authenticate(): BiometricAuthResult = suspendCancellableCoroutine { continuation ->
        val authContext = LAContext()
        authContext.evaluatePolicy(
            policy = LAPolicyDeviceOwnerAuthenticationWithBiometrics,
            localizedReason = "Authenticate to sign in"
        ) { success, error ->
            if (!continuation.isActive) return@evaluatePolicy
            val result = when {
                success -> BiometricAuthResult.Success
                error?.code?.toInt() == LAErrorUserFallback || error?.code?.toInt() == LAErrorUserCancel ->
                    BiometricAuthResult.FallbackToPassword
                else -> BiometricAuthResult.Failed
            }
            continuation.resume(result)
        }
    }
}
