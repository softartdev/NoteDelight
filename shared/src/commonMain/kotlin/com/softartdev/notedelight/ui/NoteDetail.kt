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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.presentation.note.NoteResult
import com.softartdev.notedelight.presentation.note.NoteViewModel
import com.softartdev.theme.material3.PreferableMaterialTheme
import notedelight.shared.generated.resources.Res
import notedelight.shared.generated.resources.action_delete_note
import notedelight.shared.generated.resources.action_edit_title
import notedelight.shared.generated.resources.action_save_note
import notedelight.shared.generated.resources.note_deleted
import notedelight.shared.generated.resources.note_empty
import notedelight.shared.generated.resources.note_saved
import notedelight.shared.generated.resources.type_text
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun NoteDetail(
    noteViewModel: NoteViewModel,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    LaunchedEffect(noteViewModel) {
        noteViewModel.createOrLoadNote()
    }
    val result: NoteResult by noteViewModel.stateFlow.collectAsState()
    val titleState: MutableState<String> = remember(key1 = noteViewModel, key2 = result) {
        mutableStateOf(result.note?.title ?: "")
    }
    val textState: MutableState<String> = remember(key1 = noteViewModel, key2 = result) {
        mutableStateOf(result.note?.text ?: "")
    }
    LaunchedEffect(key1 = noteViewModel, key2 = result, key3 = result.snackBarMessageType) {
        result.snackBarMessageType?.let { snackBarMessageType: NoteResult.SnackBarMessageType ->
            val msg: String = when (snackBarMessageType) {
                NoteResult.SnackBarMessageType.SAVED -> getString(Res.string.note_saved) + ": " + titleState.value
                NoteResult.SnackBarMessageType.EMPTY -> getString(Res.string.note_empty)
                NoteResult.SnackBarMessageType.DELETED -> getString(Res.string.note_deleted)
            }
            snackbarHostState.showSnackbar(message = msg)
            result.disposeOneTimeEvents()
        }
    }
    NoteDetailBody(
        result = result,
        titleState = titleState,
        textState = textState,
        snackbarHostState = snackbarHostState,
    )
    BackHandler { result.checkSaveChange(titleState.value, textState.value) }
}

@Composable
fun NoteDetailBody(
    result: NoteResult = NoteResult(),
    titleState: MutableState<String> = mutableStateOf("Title"),
    textState: MutableState<String> = mutableStateOf("Text"),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(text = titleState.value, maxLines = 1) },
            navigationIcon = {
                IconButton(onClick = {
                    result.checkSaveChange(titleState.value, textState.value)
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = Icons.AutoMirrored.Filled.ArrowBack.name
                    )
                }
            },
            actions = {
                IconButton(onClick = { result.onSaveClick(titleState.value, textState.value) }) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = stringResource(Res.string.action_save_note)
                    )
                }
                IconButton(onClick = result.onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Title,
                        contentDescription = stringResource(Res.string.action_edit_title)
                    )
                }
                IconButton(onClick = result.onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(Res.string.action_delete_note)
                    )
                }
            }
        )
    },
    content = { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (result.loading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            TextField(
                value = textState.value,
                onValueChange = { textState.value = it },
                modifier = Modifier.weight(1F).fillMaxWidth().padding(8.dp),
                label = { Text(stringResource(Res.string.type_text)) },
            )
        }
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
)

@Preview
@Composable
fun PreviewNoteDetailBody() = PreferableMaterialTheme { NoteDetailBody() }