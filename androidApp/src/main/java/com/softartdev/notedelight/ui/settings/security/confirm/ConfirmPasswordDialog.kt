package com.softartdev.notedelight.ui.settings.security.confirm

import android.content.DialogInterface
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.softartdev.notedelight.R
import com.softartdev.notedelight.ui.base.BaseDialogFragment
import com.softartdev.notedelight.util.invisible
import com.softartdev.notedelight.util.visible
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ConfirmPasswordDialog : BaseDialogFragment(
        titleStringRes = R.string.dialog_title_conform_password,
        dialogLayoutRes = R.layout.dialog_set_password
), Observer<ConfirmResult> {

    private val confirmViewModel by viewModel<ConfirmViewModel>()

    private val progressBar: ProgressBar
        get() = requireDialog().findViewById(R.id.dialog_set_progress_bar)

    private val passwordEditText: TextInputEditText
        get() = requireDialog().findViewById(R.id.set_password_edit_text)

    private val passwordTextInputLayout: TextInputLayout
        get() = requireDialog().findViewById(R.id.set_password_text_input_layout)

    private val repeatPasswordEditText: TextInputEditText
        get() = requireDialog().findViewById(R.id.repeat_set_password_edit_text)

    private val repeatPasswordTextInputLayout: TextInputLayout
        get() = requireDialog().findViewById(R.id.repeat_set_password_text_input_layout)

    override fun onShow(dialog: DialogInterface?) {
        super.onShow(dialog)
        lifecycleStateFlowJob = lifecycleScope.launch {
            confirmViewModel.resultStateFlow.onEach(::onChanged).collect()
        }
    }

    override fun onOkClicked() = confirmViewModel.conformCheck(
            password = passwordEditText.editableText,
            repeatPassword = repeatPasswordEditText.editableText
    )

    override fun onChanged(confirmResult: ConfirmResult) {
        progressBar.invisible()
        passwordTextInputLayout.error = null
        repeatPasswordTextInputLayout.error = null
        when (confirmResult) {
            ConfirmResult.InitState -> Unit
            ConfirmResult.Loading -> progressBar.visible()
            ConfirmResult.Success -> dismiss()
            is ConfirmResult.EmptyPasswordError -> {
                passwordTextInputLayout.error = requireContext().getString(R.string.empty_password)
            }
            is ConfirmResult.PasswordsNoMatchError -> {
                repeatPasswordTextInputLayout.error = requireContext().getString(R.string.passwords_do_not_match)
            }
            is ConfirmResult.Error -> showError(confirmResult.message)
        }
    }

}