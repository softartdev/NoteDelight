package com.softartdev.notedelight.interactor

data class BiometricCapability(
    val available: Boolean,
    val enrolled: Boolean,
)

expect class BiometricInteractor {
    var biometricEnabled: Boolean
    var biometricConfirmed: Boolean
    fun capability(): BiometricCapability
}
