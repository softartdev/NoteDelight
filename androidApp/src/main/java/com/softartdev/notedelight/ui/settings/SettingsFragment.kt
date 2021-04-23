package com.softartdev.notedelight.ui.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.addRepeatingJob
import androidx.preference.*
import com.softartdev.notedelight.R
import com.softartdev.notedelight.shared.createMultiplatformMessage
import com.softartdev.notedelight.ui.base.BaseDialogFragment
import com.softartdev.notedelight.ui.base.BasePrefFragment
import com.softartdev.notedelight.ui.settings.security.change.ChangePasswordDialog
import com.softartdev.notedelight.ui.settings.security.confirm.ConfirmPasswordDialog
import com.softartdev.notedelight.ui.settings.security.enter.EnterPasswordDialog
import com.softartdev.notedelight.util.ThemeHelper
import com.softartdev.notedelight.util.tintIcon
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@SuppressLint("InflateParams")
class SettingsFragment : BasePrefFragment(), Preference.OnPreferenceChangeListener, Observer<SecurityResult> {

    private val settingsViewModel by viewModel<SettingsViewModel>()
    private var securityPreferences: SwitchPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val themePreferenceCategory: PreferenceCategory? = findPreference(getString(R.string.theme))
        themePreferenceCategory?.tintIcon()

        val themePreference: ListPreference? = findPreference(getString(R.string.theme_key))
        themePreference?.onPreferenceChangeListener = this
        themePreference?.tintIcon()

        val securityPreferenceCategory: PreferenceCategory? = findPreference(getString(R.string.security))
        securityPreferenceCategory?.tintIcon()

        securityPreferences = findPreference(getString(R.string.security_key))
        securityPreferences?.onPreferenceChangeListener = this
        securityPreferences?.tintIcon()

        val passwordPreference: Preference? = findPreference(getString(R.string.password_key))
        passwordPreference?.setOnPreferenceClickListener {
            settingsViewModel.changePassword()
            true
        }
        passwordPreference?.tintIcon()

        findPreference<CheckBoxPreference>(getString(R.string.hide_screen_contents_key))?.tintIcon()

        val versionPreference = findPreference<Preference>(getString(R.string.version_key))
        versionPreference?.summary = createMultiplatformMessage()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewLifecycleOwner.addRepeatingJob(Lifecycle.State.STARTED) {
            settingsViewModel.resultStateFlow.onEach(::onChanged).collect()
        }
        settingsViewModel.checkEncryption()
    }

    override fun onChanged(securityResult: SecurityResult) = when (securityResult) {
        is SecurityResult.Loading -> Unit //TODO: progress bar
        is SecurityResult.EncryptEnable -> showEncryptEnable(securityResult.encryption)
        is SecurityResult.PasswordDialog -> showDialogFragment(EnterPasswordDialog())
        is SecurityResult.SetPasswordDialog -> showDialogFragment(ConfirmPasswordDialog())
        is SecurityResult.ChangePasswordDialog -> showDialogFragment(ChangePasswordDialog())
        is SecurityResult.Error -> showError(securityResult.message)
    }

    private fun showEncryptEnable(encryption: Boolean) = securityPreferences?.run {
        onPreferenceChangeListener = null
        isChecked = encryption
        onPreferenceChangeListener = this@SettingsFragment
    } ?: Timber.e("Security switch preference is null")

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean = when (preference?.key) {
        getString(R.string.theme_key) -> {
            ThemeHelper.applyTheme(newValue as String, requireContext())
            true
        }
        getString(R.string.security_key) -> {
            settingsViewModel.changeEncryption(newValue as Boolean)
            false
        }
        else -> throw IllegalArgumentException("Unknown preference key")
    }

    private fun showDialogFragment(dialogFragment: DialogFragment) {
        dialogFragment.setTargetFragment(this, BaseDialogFragment.DIALOG_REQUEST_CODE)
        dialogFragment.show(parentFragmentManager, "PASSWORD_DIALOG_TAG")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) = when (requestCode) {
        BaseDialogFragment.DIALOG_REQUEST_CODE -> settingsViewModel.checkEncryption()
        else -> super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showError(message: String?) {
        AlertDialog.Builder(requireContext())
                .setTitle(android.R.string.dialog_alert_title)
                .setMessage(message)
                .setNeutralButton(android.R.string.cancel, null)
                .show()
    }

}