@file:OptIn(ExperimentalMaterial3Api::class)

package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.shared.presentation.note.NoteResult
import com.softartdev.notedelight.shared.presentation.note.NoteViewModel
import com.softartdev.theme.material3.PreferableMaterialTheme
import notedelight.shared_compose_ui.generated.resources.Res
import notedelight.shared_compose_ui.generated.resources.action_delete_note
import notedelight.shared_compose_ui.generated.resources.action_edit_title
import notedelight.shared_compose_ui.generated.resources.action_save_note
import notedelight.shared_compose_ui.generated.resources.note_deleted
import notedelight.shared_compose_ui.generated.resources.note_empty
import notedelight.shared_compose_ui.generated.resources.note_saved
import notedelight.shared_compose_ui.generated.resources.type_text
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun NoteDetail(noteViewModel: NoteViewModel, noteId: Long) {
    LaunchedEffect(key1 = noteId, key2 = noteViewModel) {
        when (noteId) {
            0L -> noteViewModel.createNote()
            else -> noteViewModel.loadNote(noteId)
        }
    }
    val noteResultState: State<NoteResult> = noteViewModel.stateFlow.collectAsState()
    val titleState: MutableState<String> = remember { mutableStateOf("") }
    val textState: MutableState<String> = remember { mutableStateOf("") }
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(
        key1 = noteId,
        key2 = noteViewModel,
        key3 = noteResultState.value
    ) {
        when (val noteResult: NoteResult = noteResultState.value) {
            is NoteResult.Loading,
            is NoteResult.Created -> Unit
            is NoteResult.Loaded -> {
                titleState.value = noteResult.result.title
                textState.value = noteResult.result.text
            }
            is NoteResult.Saved -> {
                titleState.value = noteResult.title
                val noteSaved = getString(Res.string.note_saved) + ": " + noteResult.title
                snackbarHostState.showSnackbar(noteSaved)
            }
            is NoteResult.TitleUpdated -> {
                titleState.value = noteResult.title
            }
            is NoteResult.Empty -> snackbarHostState.showSnackbar(
                message = getString(Res.string.note_empty)
            )
            is NoteResult.Deleted -> {
                snackbarHostState.showSnackbar(message = getString(Res.string.note_deleted))
            }
        }
    }
    NoteDetailBody(
        snackbarHostState = snackbarHostState,
        titleState = titleState,
        textState = textState,
        onBackClick = { noteViewModel.checkSaveChange(titleState.value, textState.value) },
        onSaveClick = noteViewModel::saveNote,
        onEditClick = noteViewModel::editTitle,
        onDeleteClick = noteViewModel::subscribeToDeleteNote,
        showLoading = noteResultState.value == NoteResult.Loading,
    )
    BackHandler { noteViewModel.checkSaveChange(titleState.value, textState.value) }
}

@Composable
fun NoteDetailBody(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    titleState: MutableState<String> = mutableStateOf("Title"),
    textState: MutableState<String> = mutableStateOf("Text"),
    onBackClick: () -> Unit = {},
    onSaveClick: (title: String?, text: String) -> Unit = { _, _ -> },
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    showLoading: Boolean = true,
) = Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
        TopAppBar(
            title = { Text(text = titleState.value, maxLines = 1) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = Icons.AutoMirrored.Filled.ArrowBack.name
                    )
                }
            },
            actions = {
                IconButton(onClick = { onSaveClick(titleState.value, textState.value) }) {
                    Icon(
                        Icons.Default.Save,
                        contentDescription = stringResource(Res.string.action_save_note)
                    )
                }
                IconButton(onClick = onEditClick) {
                    Icon(
                        Icons.Default.Title,
                        contentDescription = stringResource(Res.string.action_edit_title)
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(Res.string.action_delete_note)
                    )
                }
            }
        )
    }) { paddingValues ->
    Column(
        modifier = Modifier.padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        TextField(
            value = textState.value,
            onValueChange = { textState.value = it },
            modifier = Modifier.weight(1F).fillMaxWidth().padding(8.dp),
            label = { Text(stringResource(Res.string.type_text)) },
        )
    }
}

@Preview
@Composable
fun PreviewNoteDetailBody() = PreferableMaterialTheme { NoteDetailBody() }