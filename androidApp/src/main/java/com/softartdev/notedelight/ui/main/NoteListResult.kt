package com.softartdev.notedelight.ui.main

import com.softartdev.notedelight.shared.db.Note

sealed class NoteListResult{
    object Loading : NoteListResult()
    data class Success(val result: List<Note>) : NoteListResult()
    object NavMain : NoteListResult()
    data class Error(val error: String? = null) : NoteListResult()
}