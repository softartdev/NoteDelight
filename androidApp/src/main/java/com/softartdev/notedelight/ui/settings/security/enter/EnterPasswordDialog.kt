package com.softartdev.notedelight.ui.settings.security.enter

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

class EnterPasswordDialog : BaseDialogFragment(
        titleStringRes = R.string.dialog_title_enter_password,
        dialogLayoutRes = R.layout.dialog_password
), Observer<EnterResult> {

    private val enterViewModel by viewModel<EnterViewModel>()

    private val progressBar: ProgressBar
        get() = requireDialog().findViewById(R.id.enter_progress_bar)

    private val passwordEditText: TextInputEditText
        get() = requireDialog().findViewById(R.id.enter_password_edit_text)

    private val passwordTextInputLayout: TextInputLayout
        get() = requireDialog().findViewById(R.id.enter_password_text_input_layout)

    override fun onShow(dialog: DialogInterface?) {
        super.onShow(dialog)
        lifecycleStateFlowJob = lifecycleScope.launch {
            enterViewModel.resultStateFlow.onEach(::onChanged).collect()
        }
    }

    override fun onOkClicked() = enterViewModel.enterCheck(
            password = passwordEditText.editableText
    )

    override fun onChanged(enterResult: EnterResult) {
        progressBar.invisible()
        passwordTextInputLayout.error = null
        when (enterResult) {
            EnterResult.Loading -> progressBar.visible()
            EnterResult.Success -> dismiss()
            is EnterResult.EmptyPasswordError -> {
                passwordTextInputLayout.error = requireContext().getString(R.string.empty_password)
            }
            is EnterResult.IncorrectPasswordError -> {
                passwordTextInputLayout.error = requireContext().getString(R.string.incorrect_password)
            }
            is EnterResult.Error -> showError(enterResult.message)
        }
    }

}