package com.softartdev.notedelight.ui.dialog

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.ui.NoteDetailBody

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

@Preview
@Composable
fun PreviewSaveDialog() = NoteDetailBody { SaveDialog({}, {}, {}) }

@Preview
@Composable
fun PreviewDeleteDialog() = NoteDetailBody { DeleteDialog({}, {}) }