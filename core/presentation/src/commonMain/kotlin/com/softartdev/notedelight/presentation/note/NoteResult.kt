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
    data class Save(val text: CharSequence) : NoteAction
    data object Edit : NoteAction
    data object Delete : NoteAction
    data class CheckSaveChange(val text: CharSequence) : NoteAction
    data class ShowCheckSaveChangeDialog(val text: CharSequence) : NoteAction
}
