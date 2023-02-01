package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import com.softartdev.mr.composeLocalized
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.shared.database.TestSchema
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.presentation.main.MainViewModel
import com.softartdev.notedelight.shared.presentation.main.NoteListResult

@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    onItemClicked: (id: Long) -> Unit,
    onSettingsClick: () -> Unit,
    navSignIn: () -> Unit
) {
    val noteListState: State<NoteListResult> = mainViewModel.resultStateFlow.collectAsState()
    DisposableEffect(mainViewModel) {
        mainViewModel.updateNotes()
        onDispose(mainViewModel::onCleared)
    }
    MainScreen(noteListState, onItemClicked, onSettingsClick, navSignIn)
}

@Composable
fun MainScreen(
    noteListState: State<NoteListResult>,
    onItemClicked: (id: Long) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    navSignIn: () -> Unit = {},
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(MR.strings.app_name.composeLocalized()) },
            actions = {
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = MR.strings.settings.composeLocalized())
                }
            })
    }, content = {
        when (val noteListResult = noteListState.value) {
            is NoteListResult.Loading -> Loader()
            is NoteListResult.Success -> {
                val notes: List<Note> = noteListResult.result
                if (notes.isNotEmpty()) NoteList(notes, onItemClicked) else Empty()
            }
            is NoteListResult.NavSignIn -> navSignIn()
            is NoteListResult.Error -> Error(err = noteListResult.error ?: "Error")
        }
    }, floatingActionButton = {
        val text = MR.strings.create_note.composeLocalized()
        ExtendedFloatingActionButton(
            text = { Text(text) },
            onClick = { onItemClicked(0) },
            icon = { Icon(Icons.Default.Add, contentDescription = Icons.Default.Add.name) },
            modifier = Modifier.clearAndSetSemantics { contentDescription = text }
        )
    })

@Preview
@Composable
fun PreviewMainScreen() {
    val testNotes = listOf(TestSchema.firstNote, TestSchema.secondNote, TestSchema.thirdNote)
    val noteListState: MutableState<NoteListResult> = remember {
        mutableStateOf(NoteListResult.Success(testNotes))
    }
    MainScreen(noteListState)
}