package com.softartdev.notedelight.shared.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.softartdev.notedelight.shared.presentation.main.MainViewModel
import com.softartdev.notedelight.shared.presentation.main.NoteListResult

@Composable
fun NoteListScreen(
    mainViewModel: MainViewModel = viewModel()
) {
    val noteListResult by mainViewModel.stateFlow.collectAsState()

    when (noteListResult) {
        is NoteListResult.Loading -> {
            // Show loading indicator
        }
        is NoteListResult.Success -> {
            val notes = (noteListResult as NoteListResult.Success).result
            // Display the list of notes
        }
        is NoteListResult.Error -> {
            val errorMessage = (noteListResult as NoteListResult.Error).message
            // Show error message
        }
    }
}
