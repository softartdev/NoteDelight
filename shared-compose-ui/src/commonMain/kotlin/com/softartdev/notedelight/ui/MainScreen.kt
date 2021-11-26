package com.softartdev.notedelight.ui

import com.softartdev.annotation.Preview
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import com.softartdev.mr.composeLocalized
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.di.AppModule
import com.softartdev.notedelight.shared.database.TestSchema
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.presentation.main.MainViewModel
import com.softartdev.notedelight.shared.presentation.main.NoteListResult

@Composable
fun MainScreen(
    appModule: AppModule,
    onItemClicked: (id: Long) -> Unit,
    onSettingsClick: () -> Unit,
) {
    val mainViewModel: MainViewModel = remember(appModule::mainViewModel)
    val noteListState: State<NoteListResult> = mainViewModel.resultStateFlow.collectAsState()
    DisposableEffect(mainViewModel) {
        mainViewModel.updateNotes()
        onDispose(mainViewModel::onCleared)
    }
    MainScreen(noteListState, onItemClicked, onSettingsClick)
}

@Composable
fun MainScreen(
    noteListState: State<NoteListResult>,
    onItemClicked: (id: Long) -> Unit = {},
    onSettingsClick: () -> Unit = {},
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
            is NoteListResult.NavMain -> TODO() // navigate to sign in
            is NoteListResult.Error -> Error(err = noteListResult.error ?: "Error")
        }
    }, floatingActionButton = {
        ExtendedFloatingActionButton(
            text = { Text(MR.strings.create_note.composeLocalized()) },
            onClick = { onItemClicked(0) },
            icon = { Icon(Icons.Default.Add, contentDescription = MR.strings.create_note.composeLocalized()) }
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