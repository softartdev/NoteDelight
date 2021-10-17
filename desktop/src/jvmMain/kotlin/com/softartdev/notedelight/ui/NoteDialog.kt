package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.di.AppModule

@Stable
class NoteDialog {
    var showDialog: Boolean by mutableStateOf(false)
    val dismissDialog = { showDialog = false }
    var dialogContent: @Composable () -> Unit = {}

    val showDialogIfNeed: @Composable () -> Unit = { if (showDialog) dialogContent() }

    fun showSaveChanges(saveNoteAndNavBack: () -> Unit, doNotSaveAndNavBack: () -> Unit) {
        dialogContent = { SaveDialog(saveNoteAndNavBack, doNotSaveAndNavBack, dismissDialog) }
        showDialog = true
    }

    fun showEditTitle(noteId: Long, appModule: AppModule) {
        dialogContent = { EditTitleDialog(noteId, dismissDialog, appModule) }
        showDialog = true
    }

    fun showDelete(onDeleteClick: () -> Unit) {
        dialogContent = { DeleteDialog(onDeleteClick, dismissDialog) }
        showDialog = true
    }

    fun showError(message: String?) {
        dialogContent = { ErrorDialog(message, dismissDialog) }
        showDialog = true
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SaveDialog(saveNoteAndNavBack: () -> Unit, doNotSaveAndNavBack: () -> Unit, onDismiss: () -> Unit) = AlertDialog(
    title = { Text(MR.strings.note_changes_not_saved_dialog_title.localized()) },
    text = { Text(MR.strings.note_save_change_dialog_message.localized()) },
    buttons = {
        Row {
            Button(onClick = onDismiss) { Text(MR.strings.cancel.localized()) }
            Button(onClick = doNotSaveAndNavBack) { Text(MR.strings.no.localized()) }
            Button(onClick = saveNoteAndNavBack) { Text(MR.strings.yes.localized()) }
        }
    },
    onDismissRequest = onDismiss,
)

@Composable
fun DeleteDialog(onDeleteClick: () -> Unit, onDismiss: () -> Unit) = ShowDialog(
    title = MR.strings.action_delete_note.localized(),
    text = MR.strings.note_delete_dialog_message.localized(),
    onConfirm = onDeleteClick,
    onDismiss = onDismiss
)

@Composable
fun ErrorDialog(message: String?, dismissDialog: () -> Unit) = ShowDialog(
    title = MR.strings.error_title.localized(),
    text = message,
    onConfirm = dismissDialog,
    onDismiss = dismissDialog
)

@OptIn(ExperimentalMaterialApi::class)
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
fun PreviewNoteDialog() = NoteDetailBody(showDialogIfNeed = {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SaveDialog({}, {}, {})
        DeleteDialog({}, {})
        ErrorDialog("preview err", {})
    }
})