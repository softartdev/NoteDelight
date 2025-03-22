package com.softartdev.notedelight.presentation.main

import app.cash.paging.PagingData
import com.softartdev.notedelight.model.Note
import kotlinx.coroutines.flow.Flow

sealed interface NoteListResult {
    data object Loading : NoteListResult
    data class Success(val result: Flow<PagingData<Note>>) : NoteListResult
    data class Error(val error: String? = null) : NoteListResult
}
