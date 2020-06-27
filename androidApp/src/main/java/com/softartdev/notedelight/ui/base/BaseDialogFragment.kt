package com.softartdev.notedelight.ui.base

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

abstract class BaseDialogFragment(
        @StringRes private val titleStringRes: Int,
        @LayoutRes private val dialogLayoutRes: Int
) : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(titleStringRes)
                    .setView(dialogLayoutRes)
                    .setPositiveButton(android.R.string.ok, null)
                    .setNegativeButton(android.R.string.cancel, null)
                    .create()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireDialog().setOnShowListener {
            val okButton = requireDialog().findViewById<Button>(android.R.id.button1)
            okButton.setOnClickListener { onOkClicked() }
        }
    }

    abstract fun onOkClicked()

    fun showError(message: String?) = parentFragment?.view?.let {
        showSnackbar(it, message.orEmpty())
    } ?: showToast(message)

    private fun showSnackbar(parentFragmentView: View, text: CharSequence) = Snackbar
            .make(parentFragmentView, text, Snackbar.LENGTH_LONG)
            .show()

    private fun showToast(text: String?) = Toast
            .makeText(requireContext(), text, Toast.LENGTH_LONG)
            .show()

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_FIRST_USER, null)
    }

    companion object {
        internal const val DIALOG_REQUEST_CODE = 378
    }
}