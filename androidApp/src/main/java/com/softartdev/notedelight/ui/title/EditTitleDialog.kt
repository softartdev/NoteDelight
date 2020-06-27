package com.softartdev.notedelight.ui.title

import android.os.Bundle
import android.widget.ProgressBar
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.softartdev.notedelight.R
import com.softartdev.notedelight.ui.base.BaseDialogFragment
import com.softartdev.notedelight.util.invisible
import com.softartdev.notedelight.util.visible
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel

class EditTitleDialog : BaseDialogFragment(
        titleStringRes = R.string.dialog_title_change_title,
        dialogLayoutRes = R.layout.dialog_edit_title
), Observer<EditTitleResult> {

    private val editTitleViewModel by lifecycleScope.viewModel<EditTitleViewModel>(this)

    private val noteId: Long
        get() = requireNotNull(arguments?.getLong(ARG_NOTE_ID))

    private val progressBar: ProgressBar
        get() = requireDialog().findViewById(R.id.edit_title_progress_bar)

    private val textInputLayout: TextInputLayout
        get() = requireDialog().findViewById(R.id.edit_title_text_input_layout)

    private val editText: TextInputEditText
        get() = requireDialog().findViewById(R.id.edit_title_text_input)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        editTitleViewModel.resultLiveData.observe(this as LifecycleOwner, this)
        editTitleViewModel.loadTitle(noteId)
    }

    override fun onOkClicked() = editTitleViewModel.editTitle(
            id = noteId,
            newTitle = editText.text?.toString().orEmpty()
    )

    override fun onChanged(editTitleResult: EditTitleResult) {
        progressBar.invisible()
        textInputLayout.error = null
        when (editTitleResult) {
            EditTitleResult.Loading -> progressBar.visible()
            is EditTitleResult.Loaded -> editText.setText(editTitleResult.title)
            EditTitleResult.Success -> dismiss()
            EditTitleResult.EmptyTitleError -> textInputLayout.error = getString(R.string.empty_title)
            is EditTitleResult.Error -> showError(editTitleResult.message)
        }
    }

    companion object {
        private const val ARG_NOTE_ID = "arg_note_id"

        fun create(noteId: Long): EditTitleDialog = EditTitleDialog().apply {
            arguments = Bundle().apply {
                putLong(ARG_NOTE_ID, noteId)
            }
        }
    }
}