package com.softartdev.notedelight.presentation.main

import androidx.paging.PagingData
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.model.NoteSort
import kotlinx.coroutines.flow.Flow

sealed interface NoteListResult {
    data object Loading : NoteListResult
    data class Success(
        val result: Flow<PagingData<Note>>,
        val selectedId: Long?,
        val noteSort: NoteSort = NoteSort(),
    ) : NoteListResult
    data class Error(val error: String? = null) : NoteListResult
}

sealed interface MainAction {
    data class OnNoteClick(val id: Long) : MainAction
    data class OnSortChange(val noteSort: NoteSort) : MainAction
    data object OnSettingsClick : MainAction
    data object OnRefresh : MainAction
}
