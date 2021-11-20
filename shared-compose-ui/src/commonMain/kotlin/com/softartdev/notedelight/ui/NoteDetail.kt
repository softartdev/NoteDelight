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
import com.softartdev.notedelight.shared.presentation.note.NoteResult
import com.softartdev.notedelight.shared.presentation.note.NoteViewModel
import com.softartdev.notedelight.ui.dialog.DialogHolder
import kotlinx.coroutines.launch

@Composable
fun NoteDetail(
    noteId: Long,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
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
    val titleState: MutableState<String> = remember { mutableStateOf("") }
    val textState: MutableState<String> = remember { mutableStateOf("") }

    val dialogHolder: DialogHolder = remember { DialogHolder() }

    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val snackbarHostState: SnackbarHostState = scaffoldState.snackbarHostState
    val coroutineScope = rememberCoroutineScope()
    when (val noteResult: NoteResult = noteResultState.value) {
        is NoteResult.Loading,
        is NoteResult.Created -> Unit
        is NoteResult.Loaded -> {
            titleState.value = noteResult.result.title
            textState.value = noteResult.result.text
        }
        is NoteResult.Saved -> coroutineScope.launch {
            titleState.value = noteResult.title
            val noteSaved = MR.strings.note_saved.localized() + ": " + noteResult.title
            snackbarHostState.showSnackbar(noteSaved)
        }
        is NoteResult.NavEditTitle -> dialogHolder.showEditTitle(noteId, appModule)
        is NoteResult.TitleUpdated -> {
            titleState.value = noteResult.title
        }
        is NoteResult.Empty -> coroutineScope.launch {
            snackbarHostState.showSnackbar(MR.strings.note_empty.localized())
        }
        is NoteResult.Deleted -> coroutineScope.launch {
            dialogHolder.dismissDialog()
            onBackClick()
            snackbarHostState.showSnackbar(MR.strings.note_deleted.localized())
        }
        is NoteResult.CheckSaveChange -> dialogHolder.showSaveChanges(
            saveNoteAndNavBack = { noteViewModel.saveNoteAndNavBack(titleState.value, textState.value) },
            doNotSaveAndNavBack = noteViewModel::doNotSaveAndNavBack,
        )
        is NoteResult.NavBack -> onBackClick()
        is NoteResult.Error -> dialogHolder.showError(noteResult.message)
    }
    NoteDetailBody(
        scaffoldState = scaffoldState,
        titleState = titleState,
        textState = textState,
        onBackClick = { noteViewModel.checkSaveChange(titleState.value, textState.value) },
        onSaveClick = noteViewModel::saveNote,
        onEditClick = noteViewModel::editTitle,
        onDeleteClick = { dialogHolder.showDelete(onDeleteClick = noteViewModel::deleteNote) },
        onSettingsClick = onSettingsClick,
        showLoaing = noteResultState.value == NoteResult.Loading,
        showDialogIfNeed = dialogHolder.showDialogIfNeed
    )
}

@Composable
fun NoteDetailBody(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    titleState: MutableState<String> = mutableStateOf("Title"),
    textState: MutableState<String> = mutableStateOf("Text"),
    onBackClick: () -> Unit = {},
    onSaveClick: (title: String?, text: String) -> Unit = { _, _ -> },
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    showLoaing: Boolean = true,
    showDialogIfNeed: @Composable () -> Unit = {},
) = Scaffold(
    scaffoldState = scaffoldState,
    topBar = {
        TopAppBar(
            title = { Text(text = titleState.value, maxLines = 1) },
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
    }) {
    Box {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (showLoaing) LinearProgressIndicator()
            TextField(
                value = textState.value,
                onValueChange = { textState.value = it },
                modifier = Modifier.weight(1F).fillMaxWidth().padding(8.dp),
                label = { Text(MR.strings.type_text.localized()) },
            )
        }
        showDialogIfNeed()
    }
}

@Preview
@Composable
fun PreviewNoteDetailBody() = NoteDetailBody()