package com.softartdev.notedelight.ui.settings.security.change

import android.os.Bundle
import android.widget.ProgressBar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.addRepeatingJob
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.softartdev.notedelight.R
import com.softartdev.notedelight.ui.base.BaseDialogFragment
import com.softartdev.notedelight.util.invisible
import com.softartdev.notedelight.util.visible
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

class ChangePasswordDialog : BaseDialogFragment(
        titleStringRes = R.string.dialog_title_change_password,
        dialogLayoutRes = R.layout.dialog_change_password
), Observer<ChangeResult> {

    private val changeViewModel by viewModel<ChangeViewModel>()

    private val progressBar: ProgressBar
        get() = requireDialog().findViewById(R.id.dialog_change_progress_bar)

    private val oldPasswordEditText: TextInputEditText
        get() = requireDialog().findViewById(R.id.old_password_edit_text)

    private val oldPasswordTextInputLayout: TextInputLayout
        get() = requireDialog().findViewById(R.id.old_password_text_input_layout)

    private val newPasswordEditText: TextInputEditText
        get() = requireDialog().findViewById(R.id.new_password_edit_text)

    private val newPasswordTextInputLayout: TextInputLayout
        get() = requireDialog().findViewById(R.id.new_password_text_input_layout)

    private val repeatPasswordEditText: TextInputEditText
        get() = requireDialog().findViewById(R.id.repeat_new_password_edit_text)

    private val repeatPasswordTextInputLayout: TextInputLayout
        get() = requireDialog().findViewById(R.id.repeat_new_password_text_input_layout)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewLifecycleOwner.addRepeatingJob(Lifecycle.State.STARTED) {
            changeViewModel.resultStateFlow.onEach(::onChanged).collect()
        }
    }

    override fun onOkClicked() = changeViewModel.checkChange(
            oldPassword = oldPasswordEditText.editableText,
            newPassword = newPasswordEditText.editableText,
            repeatNewPassword = repeatPasswordEditText.editableText
    )

    override fun onChanged(changeResult: ChangeResult) {
        progressBar.invisible()
        oldPasswordTextInputLayout.error = null
        newPasswordTextInputLayout.error = null
        repeatPasswordTextInputLayout.error = null
        when (changeResult) {
            ChangeResult.Loading -> progressBar.visible()
            ChangeResult.Success -> dismiss()
            ChangeResult.OldEmptyPasswordError -> {
                oldPasswordTextInputLayout.error = requireContext().getString(R.string.empty_password)
            }
            ChangeResult.NewEmptyPasswordError -> {
                newPasswordTextInputLayout.error = requireContext().getString(R.string.empty_password)
            }
            ChangeResult.PasswordsNoMatchError -> {
                repeatPasswordTextInputLayout.error = requireContext().getString(R.string.passwords_do_not_match)
            }
            ChangeResult.IncorrectPasswordError -> {
                oldPasswordTextInputLayout.error = requireContext().getString(R.string.incorrect_password)
            }
            is ChangeResult.Error -> showError(changeResult.message)
        }
    }

}