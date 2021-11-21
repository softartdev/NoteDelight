package com.softartdev.notedelight.ui.dialog

import com.softartdev.annotation.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.softartdev.mr.localized
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.di.AppModule
import com.softartdev.notedelight.shared.presentation.title.EditTitleResult
import com.softartdev.notedelight.shared.presentation.title.EditTitleViewModel
import com.softartdev.notedelight.util.AlertDialog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

@Composable
fun EditTitleDialog(noteId: Long, dismissDialog: () -> Unit, appModule: AppModule) {
    val editTitleViewModel: EditTitleViewModel = remember(noteId, appModule::editTitleViewModel)
    val editTitleResultState: State<EditTitleResult> = editTitleViewModel.resultStateFlow.collectAsState()
    DisposableEffect(noteId) {
        editTitleViewModel.loadTitle(noteId)
        onDispose(editTitleViewModel::onCleared)
    }
    var label = MR.strings.enter_title.localized()
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
            label = MR.strings.empty_title.localized()
        }
        is EditTitleResult.Error -> coroutineScope.launch {
            snackbarHostState.showSnackbar(editTitleResult.message ?: MR.strings.error_title.localized())
        }
    }
    ShowEditTitleDialog(
        showLoaing = editTitleResultState.value is EditTitleResult.Loading,
        textState = textState,
        label = label,
        isError = editTitleResultState.value is EditTitleResult.EmptyTitleError,
        snackbarHostState = snackbarHostState,
        dismissDialog = dismissDialog
    ) { editTitleViewModel.editTitle(noteId, textState.value) }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ShowEditTitleDialog(
    showLoaing: Boolean = true,
    textState: MutableState<String> = mutableStateOf("Text"),
    label: String = "Label",
    isError: Boolean = true,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    dismissDialog: () -> Unit = {},
    onEditClick: () -> Unit = {},
) = AlertDialog(
    title = { Text(text = MR.strings.dialog_title_change_title.localized()) },
    text = {
        Column {
            if (showLoaing) LinearProgressIndicator()
            TextField(
                value = textState.value,
                onValueChange = { textState.value = it },
                label = { Text(label) },
                isError = isError
            )
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    },
    confirmButton = { Button(onClick = onEditClick) { Text(MR.strings.yes.localized()) } },
    dismissButton = { Button(onClick = dismissDialog) { Text(MR.strings.cancel.localized()) } },
    onDismissRequest = dismissDialog,
)

@Preview
@Composable
fun PreviewEditTitleDialog() = PreviewDialog { ShowEditTitleDialog() }