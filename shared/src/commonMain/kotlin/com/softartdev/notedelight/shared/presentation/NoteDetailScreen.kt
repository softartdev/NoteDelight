package com.softartdev.notedelight.shared.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.softartdev.notedelight.shared.presentation.note.NoteViewModel
import com.softartdev.notedelight.shared.presentation.note.NoteResult

@Composable
fun NoteDetailScreen(
    noteViewModel: NoteViewModel = viewModel()
) {
    val noteResult by noteViewModel.stateFlow.collectAsState()

    when (noteResult) {
        is NoteResult.Loading -> {
            // Show loading indicator
        }
        is NoteResult.Success -> {
            val note = (noteResult as NoteResult.Success).note
            // Display the note details
        }
        is NoteResult.Error -> {
            val errorMessage = (noteResult as NoteResult.Error).message
            // Show error message
        }
    }
}
