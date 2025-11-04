package com.softartdev.notedelight.presentation.note

import com.softartdev.notedelight.model.Note

data class NoteResult(
    val loading: Boolean = false,
    val note: Note? = null,
) {
    fun showLoading(): NoteResult = copy(loading = true)
    fun hideLoading(): NoteResult = copy(loading = false)
}

sealed interface NoteAction {
    data class Save(val title: String?, val text: String) : NoteAction
    data object Edit : NoteAction
    data object Delete : NoteAction
    data class CheckSaveChange(val title: String, val text: String) : NoteAction
}
