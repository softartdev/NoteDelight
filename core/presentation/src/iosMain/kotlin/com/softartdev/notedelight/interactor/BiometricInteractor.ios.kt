@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package com.softartdev.notedelight.interactor

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CFBridgingRelease
import kotlinx.cinterop.CFBridgingRetain
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFMutableDictionaryRef
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFBooleanTrue
import platform.CoreFoundation.kCFTypeDictionaryKeyCallBacks
import platform.CoreFoundation.kCFTypeDictionaryValueCallBacks
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAErrorAuthenticationFailed
import platform.LocalAuthentication.LAErrorBiometryNotAvailable
import platform.LocalAuthentication.LAErrorBiometryNotEnrolled
import platform.LocalAuthentication.LAErrorPasscodeNotSet
import platform.LocalAuthentication.LAErrorSystemCancel
import platform.LocalAuthentication.LAErrorUserCancel
import platform.LocalAuthentication.LAErrorUserFallback
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics
import platform.Security.SecAccessControlCreateWithFlags
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecInteractionNotAllowed
import platform.Security.errSecItemNotFound
import platform.Security.errSecSuccess
import platform.Security.kSecAccessControlBiometryCurrentSet
import platform.Security.kSecAttrAccessControl
import platform.Security.kSecAttrAccessibleWhenUnlockedThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecReturnData
import platform.Security.kSecUseAuthenticationContext
import platform.Security.kSecUseAuthenticationUI
import platform.Security.kSecUseAuthenticationUIFail
import platform.Security.kSecValueData
import platform.darwin.OSStatus
import kotlin.coroutines.resume

actual class BiometricInteractor {

    actual suspend fun canAuthenticate(): Boolean {
        return LAContext().canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, null)
    }

    actual fun hasStoredPassword(): Boolean = memScoped {
        val service = CFBridgingRetain(SERVICE)
        val account = CFBridgingRetain(ACCOUNT)
        val query = newMutableDict()
        CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(query, kSecAttrService, service)
        CFDictionaryAddValue(query, kSecAttrAccount, account)
        CFDictionaryAddValue(query, kSecUseAuthenticationUI, kSecUseAuthenticationUIFail)
        try {
            val status = SecItemCopyMatching(query, null)
            status == errSecSuccess || status == errSecInteractionNotAllowed
        } finally {
            CFRelease(query)
            CFRelease(service)
            CFRelease(account)
        }
    }

    actual suspend fun encryptAndStorePassword(
        password: CharSequence,
        title: String,
        subtitle: String,
        negativeButton: String,
    ): BiometricResult {
        clearStoredPassword()
        val context = LAContext().apply {
            localizedFallbackTitle = ""
            localizedCancelTitle = negativeButton
        }
        val authResult = evaluatePolicy(context, "$title\n$subtitle")
        if (authResult !is BiometricResult.Success) return authResult
        val accessControl = SecAccessControlCreateWithFlags(
            allocator = null,
            protection = kSecAttrAccessibleWhenUnlockedThisDeviceOnly,
            flags = kSecAccessControlBiometryCurrentSet,
            error = null,
        ) ?: return BiometricResult.Error("Could not create access control")
        val passwordData = (NSString.create(string = password.toString()))
            .dataUsingEncoding(NSUTF8StringEncoding)
            ?: return BiometricResult.Error("Could not encode password")
        return memScoped {
            val service = CFBridgingRetain(SERVICE)
            val account = CFBridgingRetain(ACCOUNT)
            val data = CFBridgingRetain(passwordData)
            val ctxRef = CFBridgingRetain(context)
            val attrs = newMutableDict()
            CFDictionaryAddValue(attrs, kSecClass, kSecClassGenericPassword)
            CFDictionaryAddValue(attrs, kSecAttrService, service)
            CFDictionaryAddValue(attrs, kSecAttrAccount, account)
            CFDictionaryAddValue(attrs, kSecValueData, data)
            CFDictionaryAddValue(attrs, kSecAttrAccessControl, accessControl)
            CFDictionaryAddValue(attrs, kSecUseAuthenticationContext, ctxRef)
            try {
                val status = SecItemAdd(attrs, null)
                if (status == errSecSuccess) BiometricResult.Success else mapKeychainStatus(status)
            } finally {
                CFRelease(attrs)
                CFRelease(service)
                CFRelease(account)
                CFRelease(data)
                CFRelease(ctxRef)
            }
        }
    }

    actual suspend fun decryptStoredPassword(
        title: String,
        subtitle: String,
        negativeButton: String,
    ): DecryptedPasswordResult {
        if (!hasStoredPassword()) {
            return DecryptedPasswordResult.Failure(BiometricResult.Unavailable)
        }
        val context = LAContext().apply {
            localizedReason = "$title\n$subtitle"
            localizedFallbackTitle = ""
            localizedCancelTitle = negativeButton
        }
        return memScoped {
            val resultRef = alloc<CFTypeRefVar>()
            val service = CFBridgingRetain(SERVICE)
            val account = CFBridgingRetain(ACCOUNT)
            val ctxRef = CFBridgingRetain(context)
            val query = newMutableDict()
            CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
            CFDictionaryAddValue(query, kSecAttrService, service)
            CFDictionaryAddValue(query, kSecAttrAccount, account)
            CFDictionaryAddValue(query, kSecReturnData, kCFBooleanTrue)
            CFDictionaryAddValue(query, kSecUseAuthenticationContext, ctxRef)
            val status = try {
                SecItemCopyMatching(query, resultRef.ptr)
            } finally {
                CFRelease(query)
                CFRelease(service)
                CFRelease(account)
                CFRelease(ctxRef)
            }
            when (status) {
                errSecSuccess -> {
                    val data = CFBridgingRelease(resultRef.value) as? NSData
                    val pwd = data?.let { nsData ->
                        NSString.create(data = nsData, encoding = NSUTF8StringEncoding)?.toString()
                    }
                    if (pwd != null) {
                        DecryptedPasswordResult.Success(pwd)
                    } else {
                        DecryptedPasswordResult.Failure(BiometricResult.Error("Decoding failed"))
                    }
                }
                errSecItemNotFound -> {
                    clearStoredPassword()
                    DecryptedPasswordResult.Failure(BiometricResult.Unavailable)
                }
                else -> DecryptedPasswordResult.Failure(mapKeychainStatus(status))
            }
        }
    }

    actual fun clearStoredPassword() {
        memScoped {
            val service = CFBridgingRetain(SERVICE)
            val account = CFBridgingRetain(ACCOUNT)
            val query = newMutableDict()
            CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
            CFDictionaryAddValue(query, kSecAttrService, service)
            CFDictionaryAddValue(query, kSecAttrAccount, account)
            try {
                SecItemDelete(query)
            } finally {
                CFRelease(query)
                CFRelease(service)
                CFRelease(account)
            }
        }
    }

    private fun MemScope.newMutableDict(): CFMutableDictionaryRef? = CFDictionaryCreateMutable(
        kCFAllocatorDefault, 0,
        kCFTypeDictionaryKeyCallBacks.ptr,
        kCFTypeDictionaryValueCallBacks.ptr,
    )

    private suspend fun evaluatePolicy(context: LAContext, reason: String): BiometricResult =
        suspendCancellableCoroutine { continuation ->
            context.evaluatePolicy(
                policy = LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                localizedReason = reason,
            ) { success, error ->
                if (success) {
                    continuation.resume(BiometricResult.Success)
                } else {
                    val mapped = when (error?.code) {
                        LAErrorUserCancel,
                        LAErrorSystemCancel,
                        LAErrorUserFallback -> BiometricResult.Cancelled
                        LAErrorBiometryNotAvailable,
                        LAErrorBiometryNotEnrolled,
                        LAErrorPasscodeNotSet -> BiometricResult.Unavailable
                        LAErrorAuthenticationFailed -> BiometricResult.Failed
                        else -> BiometricResult.Error(
                            error?.localizedDescription ?: "LAContext error"
                        )
                    }
                    continuation.resume(mapped)
                }
            }
        }

    private fun mapKeychainStatus(status: OSStatus): BiometricResult = when (status) {
        errSecItemNotFound -> BiometricResult.Unavailable
        else -> BiometricResult.Error("Keychain status: $status")
    }

    companion object {
        private const val SERVICE = "com.softartdev.notedelight.biometric"
        private const val ACCOUNT = "db_password"
    }
}
