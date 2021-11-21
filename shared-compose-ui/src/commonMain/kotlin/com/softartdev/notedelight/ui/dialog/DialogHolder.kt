package com.softartdev.notedelight.ui.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.softartdev.annotation.Preview
import com.softartdev.mr.localized
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.di.AppModule
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

    fun showEditTitle(noteId: Long, appModule: AppModule) = showDialog {
        EditTitleDialog(noteId, dismissDialog, appModule)
    }

    fun showDelete(onDeleteClick: () -> Unit) = showDialog {
        DeleteDialog(onDeleteClick, dismissDialog)
    }

    fun showEnterPassword(appModule: AppModule) = showDialog {
        EnterPasswordDialog(dismissDialog, appModule)
    }

    fun showConfirmPassword(appModule: AppModule) = showDialog {
        ConfirmPasswordDialog(dismissDialog, appModule)
    }

    fun showChangePassword(appModule: AppModule) = showDialog {
        ChangePasswordDialog(dismissDialog, appModule)
    }

    fun showError(message: String?) = showDialog {
        ErrorDialog(message, dismissDialog)
    }
}

@Composable
fun ErrorDialog(message: String?, dismissDialog: () -> Unit) = ShowDialog(
    title = MR.strings.error_title.localized(),
    text = message,
    onConfirm = dismissDialog,
    onDismiss = dismissDialog
)

@Composable
fun ShowDialog(title: String, text: String?, onConfirm: () -> Unit, onDismiss: () -> Unit) = AlertDialog(
    title = { Text(title) },
    text = text?.let { { Text(it) } },
    confirmButton = { Button(onClick = onConfirm) { Text(MR.strings.yes.localized()) } },
    dismissButton = { Button(onClick = onDismiss) { Text(MR.strings.cancel.localized()) } },
    onDismissRequest = onDismiss,
)

@Preview
@Composable
fun PreviewErrorDialog() = PreviewDialog { ErrorDialog("preview err") {} }

@Preview
@Composable
fun PreviewDialog(dialogContent: @Composable () -> Unit) =
    Box(modifier = Modifier.fillMaxSize()) { dialogContent() }
