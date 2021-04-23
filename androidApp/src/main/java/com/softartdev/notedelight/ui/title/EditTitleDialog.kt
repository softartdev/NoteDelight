package com.softartdev.notedelight.ui.title

import android.content.DialogInterface
import android.os.Bundle
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

class EditTitleDialog : BaseDialogFragment(
        titleStringRes = R.string.dialog_title_change_title,
        dialogLayoutRes = R.layout.dialog_edit_title
), Observer<EditTitleResult> {

    private val editTitleViewModel by viewModel<EditTitleViewModel>()

    private val noteId: Long
        get() = requireNotNull(arguments?.getLong(ARG_NOTE_ID))

    private val progressBar: ProgressBar
        get() = requireDialog().findViewById(R.id.edit_title_progress_bar)

    private val textInputLayout: TextInputLayout
        get() = requireDialog().findViewById(R.id.edit_title_text_input_layout)

    private val editText: TextInputEditText
        get() = requireDialog().findViewById(R.id.edit_title_text_input)

    override fun onShow(dialog: DialogInterface?) {
        super.onShow(dialog)
        lifecycleStateFlowJob = lifecycleScope.launch {
            editTitleViewModel.resultStateFlow.onEach(::onChanged).collect()
        }
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