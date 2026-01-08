package com.softartdev.notedelight.ui.dialog.note

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.softartdev.notedelight.util.YES_BUTTON_TAG
import com.softartdev.notedelight.presentation.note.DeleteViewModel
import com.softartdev.notedelight.ui.dialog.PreviewDialog
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.action_delete_note
import notedelight.ui.shared.generated.resources.cancel
import notedelight.ui.shared.generated.resources.note_delete_dialog_message
import notedelight.ui.shared.generated.resources.yes
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DeleteDialog(deleteViewModel: DeleteViewModel) = DeleteDialog(
    onDeleteClick = deleteViewModel::deleteNoteAndNavBack,
    onDismiss = deleteViewModel::navigateUp
)

@Composable
fun DeleteDialog(onDeleteClick: () -> Unit, onDismiss: () -> Unit) = AlertDialog(
    title = { Text(text = stringResource(Res.string.action_delete_note)) },
    text = { Text(stringResource(Res.string.note_delete_dialog_message)) },
    confirmButton = {
        Button(
            modifier = Modifier.testTag(YES_BUTTON_TAG),
            onClick = onDeleteClick
        ) { Text(stringResource(Res.string.yes)) }
    },
    dismissButton = { Button(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) } },
    onDismissRequest = onDismiss,
)

@Preview
@Composable
fun PreviewDeleteDialog() = PreviewDialog { DeleteDialog({}, {}) }
