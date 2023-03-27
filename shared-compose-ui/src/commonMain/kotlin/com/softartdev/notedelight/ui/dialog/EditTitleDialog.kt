package com.softartdev.notedelight.ui.dialog

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.shared.presentation.title.EditTitleResult
import com.softartdev.notedelight.shared.presentation.title.EditTitleViewModel
import com.softartdev.themepref.AlertDialog
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

@Composable
fun EditTitleDialog(
    noteId: Long,
    dismissDialog: () -> Unit,
    editTitleViewModel: EditTitleViewModel
) {
    val editTitleResultState: State<EditTitleResult> = editTitleViewModel.resultStateFlow.collectAsState()
    DisposableEffect(noteId) {
        editTitleViewModel.loadTitle(noteId)
        onDispose(editTitleViewModel::onCleared)
    }
    var labelResource by remember { mutableStateOf(MR.strings.enter_title) }
    val textState: MutableState<String> = remember { mutableStateOf("") }
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    when (val editTitleResult: EditTitleResult = editTitleResultState.value) {
        is EditTitleResult.Loading -> Napier.d("🔁 $editTitleResult")
        is EditTitleResult.Loaded -> {
            textState.value = editTitleResult.title
        }
        is EditTitleResult.Success -> dismissDialog()
        is EditTitleResult.EmptyTitleError -> {
            labelResource = MR.strings.empty_title
        }
        is EditTitleResult.Error -> coroutineScope.launch {
            snackbarHostState.showSnackbar(editTitleResult.message ?: MR.strings.error_title.contextLocalized())
        }
    }
    ShowEditTitleDialog(
        showLoaing = editTitleResultState.value is EditTitleResult.Loading,
        textState = textState,
        labelResource = labelResource,
        isError = editTitleResultState.value is EditTitleResult.EmptyTitleError,
        snackbarHostState = snackbarHostState,
        dismissDialog = dismissDialog
    ) { editTitleViewModel.editTitle(noteId, textState.value) }
}

@Composable
fun ShowEditTitleDialog(
    showLoaing: Boolean = true,
    textState: MutableState<String> = mutableStateOf("Text"),
    labelResource: StringResource = MR.strings.enter_title,
    isError: Boolean = true,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    dismissDialog: () -> Unit = {},
    onEditClick: () -> Unit = {},
) = AlertDialog(
    title = { Text(text = stringResource(MR.strings.dialog_title_change_title)) },
    text = {
        Column {
            if (showLoaing) LinearProgressIndicator()
            TextField(
                value = textState.value,
                onValueChange = { textState.value = it },
                label = { Text(stringResource(labelResource)) },
                isError = isError,
                modifier = Modifier.semantics { contentDescription = MR.strings.enter_title.contextLocalized() }
            )
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    },
    confirmButton = { Button(onClick = onEditClick) { Text(stringResource(MR.strings.yes)) } },
    dismissButton = { Button(onClick = dismissDialog) { Text(stringResource(MR.strings.cancel)) } },
    onDismissRequest = dismissDialog,
)

@Preview
@Composable
fun PreviewEditTitleDialog() = PreviewDialog { ShowEditTitleDialog() }