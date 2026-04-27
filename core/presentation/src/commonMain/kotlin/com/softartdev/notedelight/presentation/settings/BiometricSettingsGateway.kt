package com.softartdev.notedelight.presentation.settings

interface BiometricSettingsGateway {
    fun isSupported(): Boolean
    fun isEnabled(): Boolean
    fun setEnabled(enabled: Boolean)
}

object NoOpBiometricSettingsGateway : BiometricSettingsGateway {
    override fun isSupported(): Boolean = false
    override fun isEnabled(): Boolean = false
    override fun setEnabled(enabled: Boolean) = Unit
}
