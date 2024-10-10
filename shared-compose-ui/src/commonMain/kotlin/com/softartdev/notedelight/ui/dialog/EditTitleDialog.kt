package com.softartdev.notedelight.ui.dialog

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.softartdev.notedelight.shared.presentation.title.EditTitleResult
import com.softartdev.notedelight.shared.presentation.title.EditTitleViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import notedelight.shared_compose_ui.generated.resources.Res
import notedelight.shared_compose_ui.generated.resources.cancel
import notedelight.shared_compose_ui.generated.resources.dialog_title_change_title
import notedelight.shared_compose_ui.generated.resources.empty_title
import notedelight.shared_compose_ui.generated.resources.enter_title
import notedelight.shared_compose_ui.generated.resources.error_title
import notedelight.shared_compose_ui.generated.resources.yes
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditTitleDialog(
    noteId: Long,
    editTitleViewModel: EditTitleViewModel,
) {
    LaunchedEffect(noteId) {
        editTitleViewModel.loadTitle(noteId)
    }
    val editTitleResultState: State<EditTitleResult> = editTitleViewModel.stateFlow.collectAsState()
    var labelResource by remember { mutableStateOf(Res.string.enter_title) }
    val textState: MutableState<String> = remember { mutableStateOf("") }
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    when (val editTitleResult: EditTitleResult = editTitleResultState.value) {
        is EditTitleResult.Loading -> Napier.d("🔁 $editTitleResult")
        is EditTitleResult.Loaded -> {
            textState.value = editTitleResult.title
        }
        is EditTitleResult.EmptyTitleError -> {
            labelResource = Res.string.empty_title
        }
        is EditTitleResult.Error -> coroutineScope.launch {
            snackbarHostState.showSnackbar(editTitleResult.message ?: getString(Res.string.error_title))
        }
    }
    ShowEditTitleDialog(
        showLoaing = editTitleResultState.value is EditTitleResult.Loading,
        textState = textState,
        label = stringResource(labelResource),
        isError = editTitleResultState.value is EditTitleResult.EmptyTitleError,
        snackbarHostState = snackbarHostState,
        dismissDialog = editTitleViewModel::navigateUp
    ) { editTitleViewModel.editTitle(noteId, textState.value) }
}

@Composable
fun ShowEditTitleDialog(
    showLoaing: Boolean = true,
    textState: MutableState<String> = mutableStateOf("Text"),
    label: String = stringResource(Res.string.enter_title),
    isError: Boolean = true,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    dismissDialog: () -> Unit = {},
    onEditClick: () -> Unit = {},
) = AlertDialog(
    title = { Text(text = stringResource(Res.string.dialog_title_change_title)) },
    text = {
        Column {
            if (showLoaing) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            TextField(
                value = textState.value,
                onValueChange = { textState.value = it },
                label = { Text(label) },
                isError = isError,
                modifier = Modifier.semantics { contentDescription = label }
            )
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    },
    confirmButton = { Button(onClick = onEditClick) { Text(stringResource(Res.string.yes)) } },
    dismissButton = { Button(onClick = dismissDialog) { Text(stringResource(Res.string.cancel)) } },
    onDismissRequest = dismissDialog,
)

@Preview
@Composable
fun PreviewEditTitleDialog() = PreviewDialog { ShowEditTitleDialog() }