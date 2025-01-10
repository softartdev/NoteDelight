package com.softartdev.notedelight.ui.dialog

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.softartdev.notedelight.presentation.title.EditTitleResult
import com.softartdev.notedelight.presentation.title.EditTitleViewModel
import notedelight.shared.generated.resources.Res
import notedelight.shared.generated.resources.cancel
import notedelight.shared.generated.resources.dialog_title_change_title
import notedelight.shared.generated.resources.empty_title
import notedelight.shared.generated.resources.enter_title
import notedelight.shared.generated.resources.yes
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditTitleDialog(
    editTitleViewModel: EditTitleViewModel,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    LaunchedEffect(editTitleViewModel) {
        editTitleViewModel.loadTitle()
    }
    val result: EditTitleResult by editTitleViewModel.stateFlow.collectAsState()

    LaunchedEffect(key1 = result, key2 = result, key3 = result.snackBarMessageType) {
        result.snackBarMessageType?.let { msg: String ->
            snackbarHostState.showSnackbar(msg)
            result.disposeOneTimeEvents()
        }
    }
    ShowEditTitleDialog(result)
}

@Composable
fun ShowEditTitleDialog(
    result: EditTitleResult,
    label: String = stringResource(Res.string.enter_title),
) = AlertDialog(
    title = { Text(text = stringResource(Res.string.dialog_title_change_title)) },
    text = {
        var textRange by remember { mutableStateOf(TextRange(0, result.title.length)) }
        Column {
            if (result.loading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            TextField(
                value = TextFieldValue(text = result.title, selection = textRange),
                onValueChange = {
                    textRange = it.selection
                    result.onEditTitle(it.text)
                },
                label = {
                    val res = if (result.isError) Res.string.empty_title else Res.string.enter_title
                    Text(stringResource(res))
                },
                isError = result.isError,
                modifier = Modifier.semantics { contentDescription = label }
            )
        }
    },
    confirmButton = { Button(onClick = result.onEditClick) { Text(stringResource(Res.string.yes)) } },
    dismissButton = { Button(onClick = result.onCancel) { Text(stringResource(Res.string.cancel)) } },
    onDismissRequest = result.onCancel,
)

@Preview
@Composable
fun PreviewEditTitleDialog() = PreviewDialog { ShowEditTitleDialog(EditTitleResult()) }