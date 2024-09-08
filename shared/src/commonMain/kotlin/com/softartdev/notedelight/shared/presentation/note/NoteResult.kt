package com.softartdev.notedelight.shared.presentation.note

import com.softartdev.notedelight.shared.db.Note

sealed class NoteResult {
    data object Loading : NoteResult()
    data class Created(val noteId: Long) : NoteResult()
    data class Loaded(val result: Note) : NoteResult()
    data class Saved(val title: String) : NoteResult()
    data class TitleUpdated(val title: String) : NoteResult()
    data object Empty : NoteResult()
    data object Deleted : NoteResult()
}