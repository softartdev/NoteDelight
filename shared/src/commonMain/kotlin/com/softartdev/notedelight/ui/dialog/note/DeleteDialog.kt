package com.softartdev.notedelight.ui.dialog.note

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.softartdev.notedelight.presentation.note.DeleteViewModel
import com.softartdev.notedelight.ui.dialog.PreviewDialog
import notedelight.shared.generated.resources.Res
import notedelight.shared.generated.resources.action_delete_note
import notedelight.shared.generated.resources.cancel
import notedelight.shared.generated.resources.note_delete_dialog_message
import notedelight.shared.generated.resources.yes
import org.jetbrains.compose.resources.stringResource

@Composable
fun DeleteDialog(deleteViewModel: DeleteViewModel) = DeleteDialog(
    onDeleteClick = deleteViewModel::deleteNoteAndNavBack,
    onDismiss = deleteViewModel::navigateUp
)

@Composable
fun DeleteDialog(onDeleteClick: () -> Unit, onDismiss: () -> Unit) = AlertDialog(
    title = { Text(text = stringResource(Res.string.action_delete_note)) },
    text = { Text(stringResource(Res.string.note_delete_dialog_message)) },
    confirmButton = { Button(onClick = onDeleteClick) { Text(stringResource(Res.string.yes)) } },
    dismissButton = { Button(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) } },
    onDismissRequest = onDismiss,
)

@Preview
@Composable
fun PreviewDeleteDialog() = PreviewDialog { DeleteDialog({}, {}) }
