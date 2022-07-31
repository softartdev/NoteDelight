package com.softartdev.notedelight.old.ui.settings.security.change

import android.content.DialogInterface
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.softartdev.notedelight.old.R
import com.softartdev.notedelight.old.ui.base.BaseDialogFragment
import com.softartdev.notedelight.old.util.invisible
import com.softartdev.notedelight.old.util.visible
import com.softartdev.notedelight.shared.presentation.settings.security.change.ChangeResult
import com.softartdev.notedelight.shared.presentation.settings.security.change.ChangeViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

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

    override fun onShow(dialog: DialogInterface?) {
        super.onShow(dialog)
        lifecycleStateFlowJob = lifecycleScope.launch {
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
            ChangeResult.InitState -> Unit
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