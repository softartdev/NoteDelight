package com.softartdev.notedelight.ui.dialog

import com.softartdev.annotation.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.softartdev.mr.localized
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.util.AlertDialog

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SaveDialog(saveNoteAndNavBack: () -> Unit, doNotSaveAndNavBack: () -> Unit, onDismiss: () -> Unit) = AlertDialog(
    title = { Text(MR.strings.note_changes_not_saved_dialog_title.localized()) },
    text = { Text(MR.strings.note_save_change_dialog_message.localized()) },
    buttons = {
        Row(Modifier.padding(horizontal = 8.dp)) {
            Button(onClick = onDismiss) { Text(MR.strings.cancel.localized()) }
            Spacer(Modifier.width(8.dp))
            Button(onClick = doNotSaveAndNavBack) { Text(MR.strings.no.localized()) }
            Spacer(Modifier.width(8.dp))
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
fun PreviewSaveDialog() = PreviewDialog { SaveDialog({}, {}, {}) }

@Preview
@Composable
fun PreviewDeleteDialog() = PreviewDialog { DeleteDialog({}, {}) }