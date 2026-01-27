package com.softartdev.notedelight.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.softartdev.notedelight.presentation.title.EditTitleAction
import com.softartdev.notedelight.presentation.title.EditTitleResult
import com.softartdev.notedelight.presentation.title.EditTitleViewModel
import com.softartdev.notedelight.util.ENTER_TITLE_DIALOG_TAG
import com.softartdev.notedelight.util.YES_BUTTON_TAG
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.cancel
import notedelight.ui.shared.generated.resources.dialog_title_change_title
import notedelight.ui.shared.generated.resources.empty_title
import notedelight.ui.shared.generated.resources.enter_title
import notedelight.ui.shared.generated.resources.yes
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditTitleDialog(editTitleViewModel: EditTitleViewModel) {
    LaunchedEffect(editTitleViewModel) {
        editTitleViewModel.loadTitle()
    }
    val result: EditTitleResult by editTitleViewModel.stateFlow.collectAsState()
    ShowEditTitleDialog(result, editTitleViewModel::onAction)
}

@Composable
fun ShowEditTitleDialog(
    result: EditTitleResult,
    onAction: (action: EditTitleAction) -> Unit = {},
) = AlertDialog(
    title = { Text(text = stringResource(Res.string.dialog_title_change_title)) },
    text = {
        var textRange by remember { mutableStateOf(TextRange(0, result.title.length)) }
        Column {
            if (result.loading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            TextField(
                modifier = Modifier.testTag(ENTER_TITLE_DIALOG_TAG).fillMaxWidth(),
                value = TextFieldValue(text = result.title, selection = textRange),
                onValueChange = {
                    textRange = it.selection
                    onAction(EditTitleAction.OnEditTitle(it.text))
                },
                label = {
                    val res = if (result.isError) Res.string.empty_title else Res.string.enter_title
                    Text(stringResource(res))
                },
                isError = result.isError,
                singleLine = true,
            )
        }
    },
    confirmButton = { Button(modifier = Modifier.testTag(YES_BUTTON_TAG), onClick = { onAction(EditTitleAction.OnEditClick) }) { Text(stringResource(Res.string.yes)) } },
    dismissButton = { Button(onClick = { onAction(EditTitleAction.Cancel) }) { Text(stringResource(Res.string.cancel)) } },
    onDismissRequest = { onAction(EditTitleAction.Cancel) },
)

@Preview
@Composable
fun PreviewEditTitleDialog() = PreviewDialog { ShowEditTitleDialog(EditTitleResult(loading = true)) }