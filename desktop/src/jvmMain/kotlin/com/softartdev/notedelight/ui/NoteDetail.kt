package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.di.AppModule
import com.softartdev.notedelight.shared.database.TestSchema
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.presentation.note.NoteResult
import com.softartdev.notedelight.shared.presentation.note.NoteViewModel

@Composable
fun NoteDetail(
    noteId: Long,
    onBackClick: () -> Unit,
    appModule: AppModule
) {
    val noteViewModel: NoteViewModel = remember(noteId, appModule::noteViewModel)
    val noteState: State<NoteResult> = noteViewModel.resultStateFlow.collectAsState()
    DisposableEffect(noteId) {
        when (noteId) {
            0L -> noteViewModel.createNote()
            else -> noteViewModel.loadNote(noteId)
        }
        onDispose(noteViewModel::onCleared)
    }
    when (val noteResult: NoteResult = noteState.value) {
        is NoteResult.Loading -> Loader()
        is NoteResult.Created -> NoteDetailBody(null, onBackClick)
        is NoteResult.Loaded -> NoteDetailBody(noteResult.result, onBackClick)
        is NoteResult.Error -> Error(err = noteResult.message ?: "Error")
        is NoteResult.CheckSaveChange -> TODO()
        is NoteResult.Deleted -> TODO()
        is NoteResult.Empty -> TODO()
        is NoteResult.NavBack -> TODO()
        is NoteResult.NavEditTitle -> TODO()
        is NoteResult.Saved -> TODO()
        is NoteResult.TitleUpdated -> TODO()
    }
}

@Composable
fun NoteDetailBody(
    note: Note?,
    onBackClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TopAppBar(
            title = { Text(note?.title.orEmpty()) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        )
        var text by remember { mutableStateOf(note?.text.orEmpty()) }
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1F).fillMaxWidth().padding(8.dp),
            label = { Text("Type text here") },
        )
    }
}

@Preview
@Composable
fun PreviewNoteDetailBody() {
    val onBackClick = {}
    NoteDetailBody(TestSchema.firstNote, onBackClick)
}