package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import com.softartdev.notedelight.di.AppModule
import com.softartdev.notedelight.shared.database.TestSchema
import com.softartdev.notedelight.shared.presentation.main.MainViewModel
import com.softartdev.notedelight.shared.presentation.main.NoteListResult

@Composable
fun MainScreen(
    appModule: AppModule,
    onItemClicked: (id: Long) -> Unit, // Called on item click
) {
    val mainViewModel: MainViewModel = appModule.mainViewModel
    val noteListState: State<NoteListResult> = mainViewModel.resultStateFlow.collectAsState()
    mainViewModel.updateNotes()
    MainScreen(noteListState, onItemClicked)
}

@Composable
fun MainScreen(
    noteListState: State<NoteListResult>,
    onItemClicked: (id: Long) -> Unit,
) {
    when (val noteListResult = noteListState.value) {
        is NoteListResult.Loading -> Loader()
        is NoteListResult.Success -> NotesBox(noteListResult.result, onItemClicked)
        is NoteListResult.NavMain -> TODO() // navigate to sign in
        is NoteListResult.Error -> Error(err = noteListResult.error ?: "Error")
    }
}

@Preview
@Composable
fun PreviewMainScreen() {
    val testNotes = listOf(TestSchema.firstNote, TestSchema.secondNote, TestSchema.thirdNote)
    val noteListState: MutableState<NoteListResult> = remember {
        mutableStateOf(NoteListResult.Success(testNotes))
    }
    val onItemClicked: (id: Long) -> Unit = {}
    MainScreen(noteListState, onItemClicked)
}