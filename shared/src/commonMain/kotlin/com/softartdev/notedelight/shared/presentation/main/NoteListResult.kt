package com.softartdev.notedelight.shared.presentation.main

import app.cash.paging.PagingData
import com.softartdev.notedelight.shared.db.Note
import kotlinx.coroutines.flow.Flow

sealed interface NoteListResult {
    data object Loading : NoteListResult
    data class Success(val result: Flow<PagingData<Note>>) : NoteListResult
    data class Error(val error: String? = null) : NoteListResult
}
