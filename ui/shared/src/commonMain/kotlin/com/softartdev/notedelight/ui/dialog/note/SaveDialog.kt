@file:OptIn(ExperimentalMaterial3Api::class)

package com.softartdev.notedelight.ui.dialog.note

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.presentation.note.SaveViewModel
import com.softartdev.notedelight.ui.dialog.AlertDialogContent
import com.softartdev.notedelight.ui.dialog.PreviewDialog
import com.softartdev.notedelight.util.CANCEL_BUTTON_TAG
import com.softartdev.notedelight.util.NO_BUTTON_TAG
import com.softartdev.notedelight.util.SAVE_NOTE_DIALOG_TAG
import com.softartdev.notedelight.util.YES_BUTTON_TAG
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.cancel
import notedelight.ui.shared.generated.resources.no
import notedelight.ui.shared.generated.resources.note_changes_not_saved_dialog_title
import notedelight.ui.shared.generated.resources.note_save_change_dialog_message
import notedelight.ui.shared.generated.resources.yes
import org.jetbrains.compose.resources.stringResource

@Composable
fun SaveDialog(saveViewModel: SaveViewModel) = SaveDialog(
    saveNoteAndNavBack = saveViewModel::saveNoteAndNavBack,
    doNotSaveAndNavBack = saveViewModel::doNotSaveAndNavBack,
    onDismiss = saveViewModel::navigateUp
)

@Composable
fun SaveDialog(
    saveNoteAndNavBack: () -> Unit = {},
    doNotSaveAndNavBack: () -> Unit = {},
    onDismiss: () -> Unit = {},
) = BasicAlertDialog(
    modifier = Modifier.testTag(SAVE_NOTE_DIALOG_TAG),
    content = {
        AlertDialogContent(
            buttons = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        modifier = Modifier.testTag(CANCEL_BUTTON_TAG),
                        onClick = onDismiss
                    ) { Text(stringResource(Res.string.cancel)) }
                    Button(
                        modifier = Modifier.testTag(NO_BUTTON_TAG),
                        onClick = doNotSaveAndNavBack
                    ) { Text(stringResource(Res.string.no)) }
                    Button(
                        modifier = Modifier.testTag(YES_BUTTON_TAG),
                        onClick = saveNoteAndNavBack
                    ) { Text(stringResource(Res.string.yes)) }
                }
            },
            title = { Text(stringResource(Res.string.note_changes_not_saved_dialog_title)) },
            text = { Text(stringResource(Res.string.note_save_change_dialog_message)) },
        )
    },
    onDismissRequest = onDismiss,
)

@Preview
@Composable
fun PreviewSaveDialog() = PreviewDialog { SaveDialog() }