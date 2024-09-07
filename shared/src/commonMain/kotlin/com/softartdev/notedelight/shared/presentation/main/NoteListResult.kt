package com.softartdev.notedelight.shared.presentation.main

import com.softartdev.notedelight.shared.db.Note

sealed interface NoteListResult {
    data object Loading : NoteListResult
    data class Success(val result: List<Note>) : NoteListResult
    data class Error(val error: String? = null) : NoteListResult
}
