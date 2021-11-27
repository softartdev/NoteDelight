package com.softartdev.notedelight.ui.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.softartdev.annotation.Preview
import com.softartdev.mr.composeLocalized
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.di.getViewModel
import com.softartdev.notedelight.shared.presentation.settings.security.change.ChangeViewModel
import com.softartdev.notedelight.shared.presentation.settings.security.confirm.ConfirmViewModel
import com.softartdev.notedelight.shared.presentation.settings.security.enter.EnterViewModel
import com.softartdev.notedelight.shared.presentation.title.EditTitleViewModel
import com.softartdev.notedelight.ui.dialog.security.ChangePasswordDialog
import com.softartdev.notedelight.ui.dialog.security.ConfirmPasswordDialog
import com.softartdev.notedelight.ui.dialog.security.EnterPasswordDialog
import com.softartdev.notedelight.util.AlertDialog

@Stable
class DialogHolder {
    private var showDialog: Boolean by mutableStateOf(false)
    val dismissDialog = { showDialog = false }
    var dialogContent: @Composable () -> Unit = {}
    val showDialogIfNeed: @Composable () -> Unit = { if (showDialog) dialogContent() }

    private fun showDialog(content: @Composable () -> Unit) {
        dialogContent = content
        showDialog = true
    }

    fun showSaveChanges(saveNoteAndNavBack: () -> Unit, doNotSaveAndNavBack: () -> Unit) = showDialog {
        SaveDialog(saveNoteAndNavBack, doNotSaveAndNavBack, dismissDialog)
    }

    fun showEditTitle(noteId: Long) = showDialog {
        val editTitleViewModel: EditTitleViewModel = getViewModel()
        EditTitleDialog(noteId, dismissDialog, editTitleViewModel)
    }

    fun showDelete(onDeleteClick: () -> Unit) = showDialog {
        DeleteDialog(onDeleteClick, dismissDialog)
    }

    fun showEnterPassword() = showDialog {
        val enterViewModel: EnterViewModel = getViewModel()
        EnterPasswordDialog(dismissDialog, enterViewModel)
    }

    fun showConfirmPassword() = showDialog {
        val confirmViewModel: ConfirmViewModel = getViewModel()
        ConfirmPasswordDialog(dismissDialog, confirmViewModel)
    }

    fun showChangePassword() = showDialog {
        val changeViewModel: ChangeViewModel = getViewModel()
        ChangePasswordDialog(dismissDialog, changeViewModel)
    }

    fun showError(message: String?) = showDialog {
        ErrorDialog(message, dismissDialog)
    }
}

@Composable
fun ErrorDialog(message: String?, dismissDialog: () -> Unit) = ShowDialog(
    title = MR.strings.error_title.composeLocalized(),
    text = message,
    onConfirm = dismissDialog,
    onDismiss = dismissDialog
)

@Composable
fun ShowDialog(title: String, text: String?, onConfirm: () -> Unit, onDismiss: () -> Unit) = AlertDialog(
    title = { Text(title) },
    text = text?.let { { Text(it) } },
    confirmButton = { Button(onClick = onConfirm) { Text(MR.strings.yes.composeLocalized()) } },
    dismissButton = { Button(onClick = onDismiss) { Text(MR.strings.cancel.composeLocalized()) } },
    onDismissRequest = onDismiss,
)

@Preview
@Composable
fun PreviewErrorDialog() = PreviewDialog { ErrorDialog("preview err") {} }

@Preview
@Composable
fun PreviewDialog(dialogContent: @Composable () -> Unit) =
    Box(modifier = Modifier.fillMaxSize()) { dialogContent() }
