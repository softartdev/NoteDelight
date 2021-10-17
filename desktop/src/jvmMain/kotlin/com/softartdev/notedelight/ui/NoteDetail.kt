package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.di.AppModule
import com.softartdev.notedelight.shared.database.TestSchema
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
    val deleteDialogState = remember { mutableStateOf(false) }
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    when (val noteResult: NoteResult = noteResultState.value) {
        is NoteResult.Loading -> Loader()
        is NoteResult.Created -> {
            noteState.value = null
        }
        is NoteResult.Loaded -> {
            noteState.value = noteResult.result
        }
        is NoteResult.Error -> Error(err = noteResult.message ?: "Error")
        is NoteResult.CheckSaveChange -> TODO("Not implemented yet: $noteResult")
        is NoteResult.Deleted -> coroutineScope.launch {
            snackbarHostState.showSnackbar(MR.strings.note_deleted.localized())
            deleteDialogState.value = false
            onBackClick()
        }
        is NoteResult.Empty -> coroutineScope.launch {
            snackbarHostState.showSnackbar(MR.strings.note_empty.localized())
        }
        is NoteResult.NavBack -> onBackClick()
        is NoteResult.NavEditTitle -> TODO("Not implemented yet: $noteResult")
        is NoteResult.Saved -> coroutineScope.launch {
            snackbarHostState.showSnackbar(MR.strings.note_saved.localized())
        }
        is NoteResult.TitleUpdated -> Unit
    }
    NoteDetailBody(
        note = noteState.value,
        onBackClick = onBackClick,
        onSaveClick = noteViewModel::saveNote,
        onEditClick = noteViewModel::editTitle,
        onDeleteClick = noteViewModel::deleteNote,
        onSettingsClick = ::TODO, // nav to settings
        deleteDialogState = deleteDialogState,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NoteDetailBody(
    note: Note?,
    onBackClick: () -> Unit = {},
    onSaveClick: (title: String?, text: String) -> Unit = { _, _ -> },
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    deleteDialogState: MutableState<Boolean> = mutableStateOf(false),
    snackbarHostState: SnackbarHostState = SnackbarHostState()
) = Box {
    val title by remember { mutableStateOf(note?.title.orEmpty()) }
    var text by remember { mutableStateOf(note?.text.orEmpty()) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TopAppBar(
            title = { Text(title) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            },
            actions = {
                IconButton(onClick = { onSaveClick(title, text) }) {
                    Icon(Icons.Default.Save, contentDescription = MR.strings.action_save_note.localized())
                }
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Title, contentDescription = MR.strings.action_edit_title.localized())
                }
                IconButton(onClick = { deleteDialogState.value = true }) {
                    Icon(Icons.Default.Delete, contentDescription = MR.strings.action_delete_note.localized())
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = MR.strings.settings.localized())
                }
            }
        )
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1F).fillMaxWidth().padding(8.dp),
            label = { Text("Type text here") },
        )
    }
    if (deleteDialogState.value) AlertDialog(
        title = { Text(MR.strings.action_delete_note.localized()) },
        text = { Text(MR.strings.note_delete_dialog_message.localized()) },
        onDismissRequest = { deleteDialogState.value = false },
        confirmButton = {
            Button(onClick = onDeleteClick) {
                Text(MR.strings.yes.localized())
            }
        },
        dismissButton = {
            Button(onClick = { deleteDialogState.value = false }) {
                Text(MR.strings.cancel.localized())
            }
        }
    )
    SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
}

@Preview
@Composable
fun PreviewNoteDetailBody() {
    val onBackClick = {}
    NoteDetailBody(TestSchema.firstNote, onBackClick)
}