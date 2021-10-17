package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.di.AppModule
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.presentation.note.NoteResult
import com.softartdev.notedelight.shared.presentation.note.NoteViewModel
import kotlinx.coroutines.launch

@Composable
fun NoteDetail(
    noteId: Long,
    onBackClick: () -> Unit,
    appModule: AppModule,
) {
    val noteViewModel: NoteViewModel = remember(noteId, appModule::noteViewModel)
    val noteResultState: State<NoteResult> = noteViewModel.resultStateFlow.collectAsState()
    DisposableEffect(noteId) {
        when (noteId) {
            0L -> noteViewModel.createNote()
            else -> noteViewModel.loadNote(noteId)
        }
        onDispose(noteViewModel::onCleared)
    }
    val noteState: MutableState<Note?> = remember { mutableStateOf(null) }
    val titleState: MutableState<String> = mutableStateOf(noteState.value?.title.orEmpty())
    val textState: MutableState<String> = mutableStateOf(noteState.value?.text.orEmpty())

    var showDialog: Boolean by remember { mutableStateOf(false) }
    val dismissDialog = { showDialog = false }
    var dialogContent: @Composable () -> Unit = {}

    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    when (val noteResult: NoteResult = noteResultState.value) {
        is NoteResult.Loading -> Unit
        is NoteResult.Created -> {
            noteState.value = null
        }
        is NoteResult.Loaded -> {
            noteState.value = noteResult.result
        }
        is NoteResult.Error -> {
            dialogContent = { errorDialog(noteResult.message, dismissDialog) }
            showDialog = true
        }
        is NoteResult.CheckSaveChange -> {
            dialogContent = {
                saveDialog(
                    saveNoteAndNavBack = { noteViewModel.saveNoteAndNavBack(titleState.value, textState.value) },
                    doNotSaveAndNavBack = noteViewModel::doNotSaveAndNavBack,
                    onDismiss = dismissDialog)
            }
            showDialog = true
        }
        is NoteResult.Deleted -> coroutineScope.launch {
            snackbarHostState.showSnackbar(MR.strings.note_deleted.localized())
            dismissDialog()
            onBackClick()
        }
        is NoteResult.Empty -> coroutineScope.launch {
            snackbarHostState.showSnackbar(MR.strings.note_empty.localized())
        }
        is NoteResult.NavBack -> onBackClick()
        is NoteResult.NavEditTitle -> {
            dialogContent = { EditTitleDialog(noteResult.noteId, dismissDialog, appModule) }
            showDialog = true
        }
        is NoteResult.Saved -> coroutineScope.launch {
            snackbarHostState.showSnackbar(MR.strings.note_saved.localized())
        }
        is NoteResult.TitleUpdated -> Unit
    }
    NoteDetailBody(
        titleState = titleState,
        textState = textState,
        onBackClick = onBackClick,
        onSaveClick = noteViewModel::saveNote,
        onEditClick = noteViewModel::editTitle,
        onDeleteClick = {
            dialogContent = { deleteDialog(noteViewModel::deleteNote, dismissDialog) }
            showDialog = true
        },
        onSettingsClick = ::TODO, // nav to settings
        showLoaing = noteResultState.value == NoteResult.Loading,
        showDialogIfNeed = { if (showDialog) dialogContent() },
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun NoteDetailBody(
    titleState: MutableState<String> = mutableStateOf("Title"),
    textState: MutableState<String> = mutableStateOf("Text"),
    onBackClick: () -> Unit = {},
    onSaveClick: (title: String?, text: String) -> Unit = { _, _ -> },
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    showLoaing: Boolean = true,
    showDialogIfNeed: @Composable () -> Unit = {},
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
) = Box {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TopAppBar(
            title = { Text(titleState.value) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            },
            actions = {
                IconButton(onClick = { onSaveClick(titleState.value, textState.value) }) {
                    Icon(Icons.Default.Save, contentDescription = MR.strings.action_save_note.localized())
                }
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Title, contentDescription = MR.strings.action_edit_title.localized())
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = MR.strings.action_delete_note.localized())
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = MR.strings.settings.localized())
                }
            }
        )
        if (showLoaing) LinearProgressIndicator()
        TextField(
            value = textState.value,
            onValueChange = { textState.value = it },
            modifier = Modifier.weight(1F).fillMaxWidth().padding(8.dp),
            label = { Text(MR.strings.type_text.localized()) },
        )
    }
    showDialogIfNeed()
    SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun saveDialog(saveNoteAndNavBack: () -> Unit, doNotSaveAndNavBack: () -> Unit, onDismiss: () -> Unit) = AlertDialog(
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
fun deleteDialog(onDeleteClick: () -> Unit, onDismiss: () -> Unit) = showDialog(
    title = MR.strings.action_delete_note.localized(),
    text = MR.strings.note_delete_dialog_message.localized(),
    onConfirm = onDeleteClick,
    onDismiss = onDismiss
)

@Composable
fun errorDialog(message: String?, dismissDialog: () -> Unit) = showDialog(
    title = MR.strings.error_title.localized(),
    text = message,
    onConfirm = dismissDialog,
    onDismiss = dismissDialog
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun showDialog(title: String, text: String?, onConfirm: () -> Unit, onDismiss: () -> Unit) = AlertDialog(
    title = { Text(title) },
    text = text?.let { { Text(it) } },
    confirmButton = { Button(onClick = onConfirm) { Text(MR.strings.yes.localized()) } },
    dismissButton = { Button(onClick = onDismiss) { Text(MR.strings.cancel.localized()) } },
    onDismissRequest = onDismiss,
)

@Preview
@Composable
fun PreviewNoteDetailBody() = NoteDetailBody()